package com.gtbabc.notifycenter.core.api.model;

import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import lombok.Data;

@Data
public class ChannelSendResult {

    private NotifyChannelType channelType;

    private boolean success;

    private String errorCode;

    private String errorMessage;
}