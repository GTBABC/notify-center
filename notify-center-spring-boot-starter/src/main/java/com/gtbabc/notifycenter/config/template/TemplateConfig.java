package com.gtbabc.notifycenter.config.template;

import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.TemplateFormat;
import lombok.Data;

@Data
public class TemplateConfig {
    private NotifyChannelType channel;
    private TemplateFormat format = TemplateFormat.TEXT;
    private String title;
    private String content;
}