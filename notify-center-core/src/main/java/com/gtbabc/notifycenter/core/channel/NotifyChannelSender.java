package com.gtbabc.notifycenter.core.channel;

import com.gtbabc.notifycenter.core.constant.NotifyMessage;

public interface NotifyChannelSender {

    String getChannelType();

    void send(NotifyMessage message);
}
