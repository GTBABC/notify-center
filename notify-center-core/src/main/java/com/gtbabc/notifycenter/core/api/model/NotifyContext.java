package com.gtbabc.notifycenter.core.api.model;

import com.gtbabc.notifycenter.core.constant.ChannelRule;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import com.gtbabc.notifycenter.core.constant.NotifyRule;
import com.gtbabc.notifycenter.core.constant.NotifyTemplate;
import lombok.Data;

import java.util.Map;

/**
 * 通知上下文，用于在拦截器 / 重试策略中传递信息
 */
@Data
public class NotifyContext {

    /**
     * 通知 key
     */
    private String notifyKey;

    /**
     * 原始业务参数
     */
    private Map<String, Object> params;

    /**
     * 匹配到的规则（可能为 null）
     */
    private NotifyRule rule;

    /**
     * 当前渠道对应的模板（可能为 null）
     */
    private NotifyTemplate template;

    /**
     * 当前渠道规则
     */
    private ChannelRule channelRule;

    /**
     * 当前渠道类型
     */
    private NotifyChannelType channelType;

    /**
     * 本次通知最终等级
     */
    private NotifyLevel level;

    /**
     * 将要发送 / 已发送的消息对象
     */
    private NotifyMessage message;

    /**
     * 当前重试次数（从 1 开始计数）
     */
    private int attempt;
}
