package com.gtbabc.notifycenter.channel;

import com.gtbabc.notifycenter.channel.config.DingTalkNotifyProperties;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class DingTalkNotifyChannelSender implements NotifyChannelSender {

    private final DingTalkNotifyProperties properties;

    private final RestTemplate restTemplate;

    public DingTalkNotifyChannelSender(DingTalkNotifyProperties properties, RestTemplate restTemplate) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate must not be null");
    }

    @Override
    public NotifyChannelType getChannelType() {
        return NotifyChannelType.DING_TALK;
    }

    @Override
    public void send(NotifyMessage message) {
        // Implement the logic to send a message via DingTalk Webhook
    }
}
