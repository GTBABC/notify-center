package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NotifyRule {
    private String notifyKey;
    private NotifyLevel level = NotifyLevel.INFO;
    private List<ChannelRule> channels = new ArrayList<>();
}
