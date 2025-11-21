package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.Map;

/**
 * 表示通知渠道的配置规则
 * 一个 `ChannelRule` 表示针对某一类型通知（例如 `order.timeout`）在特定渠道（如钉钉、邮件等）上的发送配置。
 */
@Data
public class ChannelRule {

    /**
     * 渠道类型，决定了消息最终发送到哪个平台（例如：钉钉、飞书、邮件等）
     */
    private NotifyChannelType channelType;

    /**
     * 使用的模板 ID，指定了消息内容的模板
     * 例如：`tpl_order_timeout_dingtalk`
     */
    private String templateId;

    /**
     * 是否启用该渠道
     * 如果为 `false`，则即使该规则生效，也不会通过该渠道发送消息
     */
    private boolean enabled = true;

    /**
     * 渠道的额外配置项
     * 可以存放该渠道的其他配置，如发送群、接收人等
     */
    private Map<String, Object> extraConfig;
}

