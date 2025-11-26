package com.gtbabc.notifycenter.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "notify.dingtalk")
public class DingTalkNotifyProperties {

    /**
     * 是否启用钉钉渠道
     */
    private boolean enabled = false;

    /**
     * 钉钉机器人 webhook
     */
    private String webhookUrl;

    /**
     * （可选）加签 secret
     */
    private String secret;
}
