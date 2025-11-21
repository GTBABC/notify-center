package com.gtbabc.notifycenter.provider;

import com.gtbabc.notifycenter.config.rule.NotifyRuleProperties;
import com.gtbabc.notifycenter.core.model.ChannelRule;
import com.gtbabc.notifycenter.core.model.NotifyRule;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlNotifyRuleProvider implements NotifyRuleProvider {

    private final NotifyRuleProperties properties;

    public YamlNotifyRuleProvider(NotifyRuleProperties properties) {
        this.properties = properties;
    }

    @Override
    public NotifyRule getRule(String notifyKey) {
        if (notifyKey == null) {
            return null;
        }
        NotifyRuleProperties.RuleConfig cfg = properties.getRules().get(notifyKey);
        if (cfg == null) {
            return null;
        }

        NotifyRule rule = new NotifyRule();
        rule.setNotifyKey(notifyKey);
        if (cfg.getLevel() != null) {
            rule.setLevel(cfg.getLevel());
        }

        List<ChannelRule> channelRules = new ArrayList<>();
        if (cfg.getChannels() != null) {
            for (NotifyRuleProperties.ChannelConfig chCfg : cfg.getChannels()) {
                if (chCfg.getChannel() == null) {
                    continue;
                }
                ChannelRule cr = new ChannelRule();
                cr.setChannelType(chCfg.getChannel());
                cr.setTemplateId(chCfg.getTemplateId());
                if (chCfg.getEnabled() != null) {
                    cr.setEnabled(chCfg.getEnabled());
                }
                Map<String, Object> extra = chCfg.getExtraConfig();
                cr.setExtraConfig(extra);
                channelRules.add(cr);
            }
        }
        rule.setChannels(channelRules);
        return rule;
    }
}
