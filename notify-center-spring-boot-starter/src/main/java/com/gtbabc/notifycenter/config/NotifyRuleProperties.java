package com.gtbabc.notifycenter.config;

import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.model.NotifyLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绑定 notify.rules.* 的配置。
 */
@ConfigurationProperties(prefix = "notify.rules")
public class NotifyRuleProperties {

    /**
     * key = notifyKey（例如 order.timeout）
     */
    private Map<String, RuleConfig> rules = new HashMap<>();

    public Map<String, RuleConfig> getRules() {
        return rules;
    }

    public void setRules(Map<String, RuleConfig> rules) {
        this.rules = rules;
    }

    public static class RuleConfig {
        private NotifyLevel level = NotifyLevel.INFO;
        private List<ChannelConfig> channels;

        public NotifyLevel getLevel() {
            return level;
        }

        public void setLevel(NotifyLevel level) {
            this.level = level;
        }

        public List<ChannelConfig> getChannels() {
            return channels;
        }

        public void setChannels(List<ChannelConfig> channels) {
            this.channels = channels;
        }
    }

    public static class ChannelConfig {
        private NotifyChannelType channel;
        private String templateId;
        private Boolean enabled = true;
        private Map<String, Object> extraConfig;

        public NotifyChannelType getChannel() {
            return channel;
        }

        public void setChannel(NotifyChannelType channel) {
            this.channel = channel;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Object> getExtraConfig() {
            return extraConfig;
        }

        public void setExtraConfig(Map<String, Object> extraConfig) {
            this.extraConfig = extraConfig;
        }
    }
}
