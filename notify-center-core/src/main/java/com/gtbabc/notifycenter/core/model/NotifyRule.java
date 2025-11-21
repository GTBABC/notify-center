package com.gtbabc.notifycenter.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示通知规则，用于定义某个通知事件（如 `order.timeout`）的发送规则
 * 一个 `NotifyRule` 对象指定了某一通知事件的级别、渠道及其相应的发送规则。
 */
@Data
public class NotifyRule {

    /**
     * 通知的唯一标识符，用于标识该规则对应的通知事件
     * 例如：`order.timeout`, `order.created` 等
     */
    private String notifyKey;

    /**
     * 通知的默认等级，表示该通知的严重程度
     * 可以是：`INFO`、`ALERT`、`CRITICAL` 等，默认值为 `INFO`
     */
    private NotifyLevel level = NotifyLevel.INFO;

    /**
     * 与此通知事件相关的渠道规则列表
     * 每个 `ChannelRule` 定义了一个渠道的发送方式（例如钉钉、飞书、邮件等）
     * 通常会根据每个渠道的特性和配置选择不同的模板、接收人等
     */
    private List<ChannelRule> channels = new ArrayList<>();
}
