package com.gtbabc.notifycenter.core.api;

import com.gtbabc.notifycenter.core.api.model.NotifyContext;

/**
 * 重试策略
 * 决定是否需要重试以及下一次重试的延迟时间
 */
public interface RetryPolicy {

    /**
     * 是否在当前 attempt 后进行重试
     *
     * @param context       通知上下文
     * @param attempt       当前是第几次尝试（从 1 开始）
     * @param lastException 最近一次发送失败的异常
     */
    boolean shouldRetry(NotifyContext context, int attempt, Exception lastException);

    /**
     * 下一次重试前等待的毫秒数
     * 返回 <= 0 表示不需要等待
     */
    long nextDelayMillis(NotifyContext context, int attempt, Exception lastException);
}
