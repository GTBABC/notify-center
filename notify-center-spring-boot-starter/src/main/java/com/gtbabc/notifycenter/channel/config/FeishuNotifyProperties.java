package com.gtbabc.notifycenter.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "notify.feishu")
public class FeishuNotifyProperties {

    /**
     * 是否启用 Feishu sender
     */
    private boolean enabled = false;

    /**
     * 飞书机器人 webhook 地址
     */
    private String webhookUrl;

    /**
     * 签名 secret（如果使用安全设置，可选）
     */
    private String secret;
}
