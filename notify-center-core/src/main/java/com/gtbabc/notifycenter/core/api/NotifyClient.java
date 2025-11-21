package com.gtbabc.notifycenter.core.api;

import com.gtbabc.notifycenter.core.model.NotifyLevel;

import java.util.Map;

/**
 * 统一通知入口。
 */
public interface NotifyClient {

    void notify(String notifyKey, Map<String, Object> params);

    void notify(String notifyKey, NotifyLevel level, Map<String, Object> params);
}
