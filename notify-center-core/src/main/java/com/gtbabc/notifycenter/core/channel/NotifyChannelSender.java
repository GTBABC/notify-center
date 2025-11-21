package com.gtbabc.notifycenter.core.channel;

import com.gtbabc.notifycenter.core.model.NotifyChannelType;
import com.gtbabc.notifycenter.core.model.NotifyMessage;

public interface NotifyChannelSender {

    NotifyChannelType getChannelType();

    void send(NotifyMessage message);
}
