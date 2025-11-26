package com.gtbabc.notifycenter.core.constant;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotifyMessage {

    /**
     * 通知唯一标识（规则 key）
     */
    private String notifyKey;

    /**
     * 通知等级
     */
    private NotifyLevel level;

    /**
     * 渠道类型
     */
    private NotifyChannelType channelType;

    /**
     * 渠道模板格式（TEXT / MARKDOWN / HTML ...）
     */
    private TemplateFormat format;

    /**
     * 标题（渲染后的）
     */
    private String title;

    /**
     * 内容（渲染后的）
     */
    private String content;

    /**
     * 业务上下文参数（调用 notify 时传入的 params）
     */
    private Map<String, Object> context;

    /**
     * 渠道额外配置（来自 ChannelRule.extraConfig）
     * 例如：群、@人配置、机器人配置等
     */
    private Map<String, Object> channelConfig;

    /**
     * 接收人列表（如果你在 Sender / 业务层有使用，可以统一塞到这里）
     */
    private List<String> receivers;
}