package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotifyMessage {
    private String notifyKey;
    private NotifyChannelType channelType;
    private NotifyLevel level;
    private String title;
    private String content;
    private List<String> receivers;
    private Map<String, Object> context;
}
