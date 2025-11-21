package com.gtbabc.notifycenter.config;

import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.model.NotifyTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定 notify.templates.* 的配置。
 */
@ConfigurationProperties(prefix = "notify.templates")
public class NotifyTemplateProperties {

    /**
     * key = templateId
     */
    private Map<String, TemplateConfig> templates = new HashMap<>();

    public Map<String, TemplateConfig> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, TemplateConfig> templates) {
        this.templates = templates;
    }

    public static class TemplateConfig {
        private NotifyChannelType channel;
        private NotifyTemplate.TemplateFormat format = NotifyTemplate.TemplateFormat.TEXT;
        private String title;
        private String content;

        public NotifyChannelType getChannel() {
            return channel;
        }

        public void setChannel(NotifyChannelType channel) {
            this.channel = channel;
        }

        public NotifyTemplate.TemplateFormat getFormat() {
            return format;
        }

        public void setFormat(NotifyTemplate.TemplateFormat format) {
            this.format = format;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
