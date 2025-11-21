package com.gtbabc.notifycenter.core.model;

import lombok.Data;

@Data
public class NotifyTemplate {
    private String templateId;
    private NotifyChannelType channelType;
    private TemplateFormat format = TemplateFormat.TEXT;
    private String titleTemplate;
    private String contentTemplate;
}
