package com.gtbabc.notifycenter.config.rule;

import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import lombok.Data;

import java.util.Map;

@Data
public class ChannelConfig {
    private NotifyChannelType channel;
    private String templateId;
    private Boolean enabled = true;
    private Map<String, Object> extraConfig;
}