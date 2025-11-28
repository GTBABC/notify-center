package com.gtbabc.notifycenter.core.api.impl;

import com.gtbabc.notifycenter.core.api.AsyncNotifyClient;
import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.api.model.NotifyResult;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 基于 Executor 的简单异步实现
 * 线程池由外部（starter/业务）提供
 */
public class ExecutorAsyncNotifyClient implements AsyncNotifyClient {

    private final NotifyClient delegate;
    private final Executor executor;

    public ExecutorAsyncNotifyClient(NotifyClient delegate, Executor executor) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    @Override
    public CompletableFuture<NotifyResult> notifyAsync(String notifyKey, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(
                () -> delegate.notify(notifyKey, params),
                executor
        );
    }

    @Override
    public CompletableFuture<NotifyResult> notifyAsync(String notifyKey, NotifyLevel level, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(
                () -> delegate.notify(notifyKey, level, params),
                executor
        );
    }

    @Override
    public CompletableFuture<NotifyResult> notifyAsync(String notifyKey, String prefixTemplateId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(
                () -> delegate.notify(notifyKey, prefixTemplateId, params),
                executor
        );
    }

    @Override
    public CompletableFuture<NotifyResult> notifyAsync(String notifyKey, String prefixTemplateId, NotifyLevel level, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(
                () -> delegate.notify(notifyKey, prefixTemplateId, level, params),
                executor
        );
    }
}
