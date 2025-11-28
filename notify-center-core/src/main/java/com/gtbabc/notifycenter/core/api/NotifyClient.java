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
     * @param prefixTemplateId 模版前缀，会根据 prefixTemplateId + "_" + channelType 生成模版ID
     * @param params           参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, String prefixTemplateId, Map<String, Object> params);

    /**
     * 指定模板等级发送通知
     *
     * @param notifyKey        规则key
     * @param prefixTemplateId 模版前缀，会根据 prefixTemplateId + "_" + channelType 生成模版ID
     * @param level            通知级别
     * @param params           参数
     * @return NotifyResult
     */
    NotifyResult notify(String notifyKey, String prefixTemplateId, NotifyLevel level, Map<String, Object> params);
}


