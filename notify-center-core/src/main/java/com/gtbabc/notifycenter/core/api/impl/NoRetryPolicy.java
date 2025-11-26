package com.gtbabc.notifycenter.core.api.impl;

import com.gtbabc.notifycenter.core.api.RetryPolicy;
import com.gtbabc.notifycenter.core.api.model.NotifyContext;

/**
 * 默认重试策略：不重试
 */
public class NoRetryPolicy implements RetryPolicy {

    @Override
    public boolean shouldRetry(NotifyContext context, int attempt, Exception lastException) {
        return false;
    }

    @Override
    public long nextDelayMillis(NotifyContext context, int attempt, Exception lastException) {
        return 0L;
    }
}
