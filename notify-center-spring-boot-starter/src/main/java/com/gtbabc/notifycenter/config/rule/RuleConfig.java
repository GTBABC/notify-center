package com.gtbabc.notifycenter.config.rule;

import com.gtbabc.notifycenter.core.model.NotifyLevel;
import lombok.Data;

import java.util.List;

@Data
public class RuleConfig {
    private NotifyLevel level = NotifyLevel.INFO;
    private List<ChannelConfig> channels;
}