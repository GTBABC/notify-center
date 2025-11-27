package com.gtbabc.notifycenter.config;

import com.gtbabc.notifycenter.channel.DingTalkNotifyChannelSender;
import com.gtbabc.notifycenter.channel.FeishuNotifyChannelSender;
import com.gtbabc.notifycenter.channel.MailNotifyChannelSender;
import com.gtbabc.notifycenter.channel.config.DingTalkNotifyProperties;
import com.gtbabc.notifycenter.channel.config.FeishuNotifyProperties;
import com.gtbabc.notifycenter.channel.config.MailNotifyProperties;
import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.api.impl.DefaultNotifyClient;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import com.gtbabc.notifycenter.core.template.TemplateEngine;
import com.gtbabc.notifycenter.core.template.impl.SimpleTemplateEngine;
import com.gtbabc.notifycenter.provider.YamlNotifyRuleProvider;
import com.gtbabc.notifycenter.provider.YamlNotifyTemplateProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfiguration
@EnableConfigurationProperties({FeishuNotifyProperties.class, MailNotifyProperties.class, DingTalkNotifyProperties.class})
public class NotifyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TemplateEngine.class)
    public TemplateEngine templateEngine() {
        return new SimpleTemplateEngine();
    }

    @Bean
    @ConditionalOnMissingBean(NotifyRuleProvider.class)
    public NotifyRuleProvider notifyRuleProvider() {
        return new YamlNotifyRuleProvider();
    }

    @Bean
    @ConditionalOnMissingBean(NotifyTemplateProvider.class)
    public NotifyTemplateProvider notifyTemplateProvider() {
        return new YamlNotifyTemplateProvider();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(FeishuNotifyChannelSender.class)
    public FeishuNotifyChannelSender feishuNotifyChannelSender(FeishuNotifyProperties properties, RestTemplate restTemplate) {
        return new FeishuNotifyChannelSender(properties, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(MailNotifyChannelSender.class)
    public MailNotifyChannelSender mailNotifyChannelSender(MailNotifyProperties properties, JavaMailSender mailSender) {
        return new MailNotifyChannelSender(properties, mailSender);
    }

    @Bean
    @ConditionalOnMissingBean(DingTalkNotifyChannelSender.class)
    public DingTalkNotifyChannelSender dingTalkNotifyChannelSender(DingTalkNotifyProperties properties, RestTemplate restTemplate) {
        return new DingTalkNotifyChannelSender(properties, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(NotifyClient.class)
    public NotifyClient notifyClient(NotifyRuleProvider ruleProvider,
                                     NotifyTemplateProvider templateProvider,
                                     TemplateEngine templateEngine,
                                     List<NotifyChannelSender> senders) {

        Map<String, NotifyChannelSender> senderMap = senders.stream()
                .collect(Collectors.toMap(NotifyChannelSender::getChannelType, s -> s));

        return new DefaultNotifyClient(ruleProvider, templateProvider, templateEngine, senderMap);
    }
}
