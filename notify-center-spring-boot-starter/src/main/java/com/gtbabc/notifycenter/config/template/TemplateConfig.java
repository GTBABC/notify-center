package com.gtbabc.notifycenter.config.template;

import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.model.TemplateFormat;
import lombok.Data;

@Data
public class TemplateConfig {
    private NotifyChannelType channel;
    private TemplateFormat format = TemplateFormat.TEXT;
    private String title;
    private String content;
}