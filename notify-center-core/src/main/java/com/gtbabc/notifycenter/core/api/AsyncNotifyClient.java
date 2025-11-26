package com.gtbabc.notifycenter.core.api;

import com.gtbabc.notifycenter.core.api.model.NotifyResult;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 异步通知客户端
 * 基于 CompletableFuture 封装
 */
public interface AsyncNotifyClient {

    CompletableFuture<NotifyResult> notifyAsync(String notifyKey, Map<String, Object> params);

    CompletableFuture<NotifyResult> notifyAsync(String notifyKey, NotifyLevel level, Map<String, Object> params);
}
