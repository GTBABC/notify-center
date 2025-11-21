package com.gtbabc.notifycenter.config.template;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定 notify.templates.* 的配置。
 */
@ConfigurationProperties(prefix = "notify.templates")
@Data
public class NotifyTemplateProperties {

    /**
     * key = templateId
     */
    private Map<String, TemplateConfig> templates = new HashMap<>();
}
