package com.gtbabc.notifycenter.core.api;

import com.gtbabc.notifycenter.core.api.model.NotifyResult;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;

import java.util.Map;

/**
 * 统一通知入口。
 */
public interface NotifyClient {

    /**
     * 发送通知
     */
    NotifyResult notify(String notifyKey, Map<String, Object> params);

    /**
     * 指定等级发送通知
     */
    NotifyResult notify(String notifyKey, NotifyLevel level, Map<String, Object> params);
}


