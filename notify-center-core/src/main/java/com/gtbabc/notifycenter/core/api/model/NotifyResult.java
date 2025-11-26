package com.gtbabc.notifycenter.core.api.model;

import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;
import lombok.Data;

import java.util.Map;

@Data
public class NotifyResult {

    private String notifyKey;

    private boolean ruleFound;

    private NotifyLevel finalLevel;

    /**
     * 每个渠道的发送结果
     */
    private Map<NotifyChannelType, ChannelSendResult> channelResults;
}