package com.gtbabc.notifycenter.core.constant;

import lombok.Data;

/**
 * 表示通知模板，定义了通知的标题和内容格式
 * `NotifyTemplate` 用于描述如何展示通知的内容，包括通知的标题模板、内容模板及其格式。
 */
@Data
public class NotifyTemplate {

    /**
     * 模板的唯一标识符，用于区分不同的模板
     * 例如：`tpl_order_timeout_dingtalk`
     */
    private String templateId;

    /**
     * 渠道类型，指定该模板适用的渠道（例如钉钉、飞书、邮件等）
     * 不同渠道可能会使用不同的模板格式
     */
    private NotifyChannelType channelType;

    /**
     * 模板的格式，决定通知的呈现形式
     * 默认为 `TEXT`，可选择 `TEXT`、`MARKDOWN` 或 `HTML` 格式
     */
    private TemplateFormat format = TemplateFormat.TEXT;

    /**
     * 通知的标题模板，包含占位符（例如 `${orderId}`），用来生成实际的标题
     * 例如：`【订单超时】订单${orderId}`
     */
    private String titleTemplate;

    /**
     * 通知的内容模板，包含占位符，用来生成实际的通知内容
     * 例如：`订单 ${orderId} 已超时 ${timeoutMinutes} 分钟`
     */
    private Object contentTemplate;
}
