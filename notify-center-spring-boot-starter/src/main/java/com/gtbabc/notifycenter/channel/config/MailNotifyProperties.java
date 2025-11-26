package com.gtbabc.notifycenter.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "notify.mail")
public class MailNotifyProperties {

    /**
     * 是否启用邮件 sender
     */
    private boolean enabled = false;

    /**
     * 默认发件人
     */
    private String from;

    /**
     * 默认收件人列表（可选，真正收件人也可以从 NotifyMessage.channelConfig 中传）
     */
    private List<String> to;
}
