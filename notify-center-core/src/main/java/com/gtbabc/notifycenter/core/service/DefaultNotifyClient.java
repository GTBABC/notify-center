package com.gtbabc.notifycenter.core.service;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.model.ChannelRule;
import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.model.NotifyLevel;
import com.gtbabc.notifycenter.core.model.NotifyMessage;
import com.gtbabc.notifycenter.core.model.NotifyRule;
import com.gtbabc.notifycenter.core.model.NotifyTemplate;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import com.gtbabc.notifycenter.core.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultNotifyClient implements NotifyClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultNotifyClient.class);

    private final NotifyRuleProvider ruleProvider;
    private final NotifyTemplateProvider templateProvider;
    private final TemplateEngine templateEngine;
    private final Map<NotifyChannelType, NotifyChannelSender> senderMap;

    public DefaultNotifyClient(NotifyRuleProvider ruleProvider,
                               NotifyTemplateProvider templateProvider,
                               TemplateEngine templateEngine,
                               Map<NotifyChannelType, NotifyChannelSender> senderMap) {
        this.ruleProvider = ruleProvider;
        this.templateProvider = templateProvider;
        this.templateEngine = templateEngine;
        this.senderMap = senderMap;
    }

    @Override
    public void notify(String notifyKey, Map<String, Object> params) {
        notify(notifyKey, null, params);
    }

    @Override
    public void notify(String notifyKey, NotifyLevel overrideLevel, Map<String, Object> params) {
        if (notifyKey == null || notifyKey.isEmpty()) {
            log.warn("notifyKey is blank, skip notify. params={}", params);
            return;
        }

        NotifyRule rule = ruleProvider.getRule(notifyKey);
        if (rule == null) {
            log.warn("No notify rule found for key={}, skip notify. params={}", notifyKey, params);
            return;
        }

        NotifyLevel level = (overrideLevel != null) ? overrideLevel : rule.getLevel();

        if (rule.getChannels() == null || rule.getChannels().isEmpty()) {
            log.warn("No channels configured for notifyKey={}, skip notify.", notifyKey);
            return;
        }

        for (ChannelRule channelRule : rule.getChannels()) {
            if (channelRule == null || !channelRule.isEnabled()) {
                continue;
            }

            NotifyChannelType channelType = channelRule.getChannelType();
            String templateId = channelRule.getTemplateId();

            NotifyTemplate template = templateProvider.getTemplate(templateId);
            if (template == null) {
                log.warn("No template found for templateId={} (notifyKey={}, channel={})",
                        templateId, notifyKey, channelType);
                continue;
            }

            if (template.getChannelType() != null && template.getChannelType() != channelType) {
                log.warn("Template channelType mismatch, templateId={}, templateChannel={}, ruleChannel={}",
                        templateId, template.getChannelType(), channelType);
            }

            String title = templateEngine.render(template.getTitleTemplate(), params);
            String content = templateEngine.render(template.getContentTemplate(), params);

            NotifyMessage message = new NotifyMessage();
            message.setNotifyKey(notifyKey);
            message.setChannelType(channelType);
            message.setLevel(level);
            message.setTitle(title);
            message.setContent(content);
            message.setContext(params);

            NotifyChannelSender sender = senderMap.get(channelType);
            if (sender == null) {
                log.warn("No sender found for channelType={}, skip. notifyKey={}", channelType, notifyKey);
                continue;
            }

            try {
                sender.send(message);
            } catch (Exception e) {
                log.error("Failed to send notify. notifyKey={}, channelType={}, templateId={}",
                        notifyKey, channelType, templateId, e);
            }
        }
    }
}
