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
     *
     * @param notifyKey 规则key
     * @param params    参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, Map<String, Object> params);

    /**
     * 指定等级发送通知
     *
     * @param notifyKey 规则key
     * @param level     通知级别
     * @param params    参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, NotifyLevel level, Map<String, Object> params);

    /**
     * 指定模板发送通知
     *
     * @param notifyKey        规则key
     * @param templateId 模版id, 模版id下有多个类型的模版，如：钉钉，飞书等
     * @param params           参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, String templateId, Map<String, Object> params);

    /**
     * 指定模板等级发送通知
     *
     * @param notifyKey        规则key
     * @param templateId 模版id, 模版id下有多个类型的模版，如：钉钉，飞书等
     * @param level            通知级别
     * @param params           参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, String templateId, NotifyLevel level, Map<String, Object> params);
}


