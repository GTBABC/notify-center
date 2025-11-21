package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示通知消息的内容
 * 一个 `NotifyMessage` 对象包含了通知的详细信息，包括通知的关键字、渠道、内容、接收人等。
 */
@Data
public class NotifyMessage {

    /**
     * 通知的唯一标识符，通常是通知事件的标识（例如：`order.timeout`）
     * 用来匹配对应的规则，触发发送通知
     */
    private String notifyKey;

    /**
     * 渠道类型，指明通知将通过哪个渠道发送（如钉钉、飞书、邮件等）
     */
    private NotifyChannelType channelType;

    /**
     * 通知等级，表示通知的严重程度（如：`INFO`, `ALERT`, `CRITICAL`）
     */
    private NotifyLevel level;

    /**
     * 通知的标题，通常会作为消息的头部信息显示
     * 例如：`订单超时通知`
     */
    private String title;

    /**
     * 通知的具体内容，通常为通知的详细信息，支持模板渲染
     * 例如：`订单 ${orderId} 已超时 ${timeoutMinutes} 分钟`
     */
    private String content;

    /**
     * 接收人列表，表示该通知将发送给哪些用户/群体
     * 可以为空，表示默认发送给所有相关接收人
     */
    private List<String> receivers;

    /**
     * 上下文信息，包含原始通知参数以及任何额外的调试信息
     * 例如：`{"orderId": 12345, "userId": 67890, "timeoutMinutes": 30}`
     */
    private Map<String, Object> context;
}
