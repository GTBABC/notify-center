package com.gtbabc.notifycenter.channel;

import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 钉钉 Sender 占位实现：先把要发的内容打日志。
 * 之后你可以在这里用 RestTemplate 调 webhook。
 */
public class DingTalkNotifyChannelSender implements NotifyChannelSender {

    private static final Logger log = LoggerFactory.getLogger(DingTalkNotifyChannelSender.class);

    @Override
    public NotifyChannelType getChannelType() {
        return NotifyChannelType.DING_TALK;
    }

    @Override
    public void send(NotifyMessage message) {
        // TODO: 这里换成真正的钉钉机器人调用
        log.info("[DING_TALK] notifyKey={}, title={}, content={}",
                message.getNotifyKey(), message.getTitle(), message.getContent());
    }
}
