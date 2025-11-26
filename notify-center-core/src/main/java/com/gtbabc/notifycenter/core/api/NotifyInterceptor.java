package com.gtbabc.notifycenter.core.api;

import com.gtbabc.notifycenter.core.api.model.ChannelSendResult;
import com.gtbabc.notifycenter.core.api.model.NotifyContext;

/**
 * 通知拦截器
 * 可以用于埋点、审计、灰度、动态过滤等
 */
public interface NotifyInterceptor {

    /**
     * 每次实际发送前调用（每一次重试也会调用）
     */
    default void beforeSend(NotifyContext context) {
    }

    /**
     * 每个渠道最终发送完成后调用（不管成功或失败）
     */
    default void afterSend(NotifyContext context, ChannelSendResult channelResult) {
    }
}
