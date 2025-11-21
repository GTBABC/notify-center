package com.gtbabc.notifycenter.config;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import com.gtbabc.notifycenter.core.service.DefaultNotifyClient;
import com.gtbabc.notifycenter.core.template.SimpleTemplateEngine;
import com.gtbabc.notifycenter.core.template.TemplateEngine;
import com.gtbabc.notifycenter.channel.DingTalkNotifyChannelSender;
import com.gtbabc.notifycenter.provider.YamlNotifyRuleProvider;
import com.gtbabc.notifycenter.provider.YamlNotifyTemplateProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfiguration
@EnableConfigurationProperties({NotifyRuleProperties.class, NotifyTemplateProperties.class})
public class NotifyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TemplateEngine.class)
    public TemplateEngine templateEngine() {
        return new SimpleTemplateEngine();
    }

    @Bean
    @ConditionalOnMissingBean(NotifyRuleProvider.class)
    public NotifyRuleProvider notifyRuleProvider(NotifyRuleProperties props) {
        return new YamlNotifyRuleProvider(props);
    }

    @Bean
    @ConditionalOnMissingBean(NotifyTemplateProvider.class)
    public NotifyTemplateProvider notifyTemplateProvider(NotifyTemplateProperties props) {
        return new YamlNotifyTemplateProvider(props);
    }

    @Bean
    @ConditionalOnMissingBean(DingTalkNotifyChannelSender.class)
    public DingTalkNotifyChannelSender dingTalkNotifyChannelSender() {
        return new DingTalkNotifyChannelSender();
    }

    @Bean
    @ConditionalOnMissingBean(NotifyClient.class)
    public NotifyClient notifyClient(NotifyRuleProvider ruleProvider,
                                     NotifyTemplateProvider templateProvider,
                                     TemplateEngine templateEngine,
                                     List<NotifyChannelSender> senders) {

        Map<NotifyChannelType, NotifyChannelSender> senderMap = senders.stream()
                .collect(Collectors.toMap(NotifyChannelSender::getChannelType, s -> s));

        return new DefaultNotifyClient(ruleProvider, templateProvider, templateEngine, senderMap);
    }
}
