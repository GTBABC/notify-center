package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.Map;

@Data
public class ChannelRule {
    private NotifyChannelType channelType;
    private String templateId;
    private boolean enabled = true;
    private Map<String, Object> extraConfig;
}
