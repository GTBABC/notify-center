package com.gtbabc.notifycenter.core.channel;

import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;

public interface NotifyChannelSender {

    NotifyChannelType getChannelType();

    void send(NotifyMessage message);
}
