package com.gtbabc.notifycenter.core.constant;

/**
 * 通知渠道类型枚举
 * `NotifyChannelType` 用于定义消息发送的目标渠道。不同的渠道可能具有不同的处理逻辑、模板和展示方式。
 * 例如，钉钉、飞书、邮件等可以作为发送通知的目标渠道。
 */
public enum NotifyChannelType {

    /**
     * 钉钉，通常用于企业内外部沟通
     * 发送通知时，可以选择不同的钉钉群组或用户
     */
    DING_TALK,

    /**
     * 飞书，类似钉钉的企业即时通讯工具
     * 用于企业内部通知，支持群消息或私聊消息
     */
    FEI_SHU,

    /**
     * 邮件，传统的电子邮件发送方式
     * 通过 SMTP 发送，适合发送格式化的通知和报告
     */
    EMAIL,


    /**
     * 短信，用于发送手机短信
     */
    SMS,

    /**
     * 企业微信，用于企业内部沟通
     */
    WEWORK,

    /**
     * Slack，用于企业内部沟通
     */
    SLACK,

    /**
     * 微信，用于企业内部沟通
     */
    PUSH_NOTIFICATION;
}
