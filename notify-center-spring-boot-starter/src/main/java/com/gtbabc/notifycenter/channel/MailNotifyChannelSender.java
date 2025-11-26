package com.gtbabc.notifycenter.channel;

import com.gtbabc.notifycenter.channel.config.MailNotifyProperties;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 基于 Spring JavaMailSender 的邮件 Sender 实现
 */
@Slf4j
public class MailNotifyChannelSender implements NotifyChannelSender {

    private final MailNotifyProperties properties;
    private final JavaMailSender mailSender;

    public MailNotifyChannelSender(MailNotifyProperties properties, JavaMailSender mailSender) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.mailSender = Objects.requireNonNull(mailSender, "mailSender must not be null");
    }

    /**
     * 返回当前发送器对应的渠道类型
     */
    @Override
    public NotifyChannelType getChannelType() {
        return NotifyChannelType.EMAIL;  // 返回邮件渠道类型
    }

    @Override
    public void send(NotifyMessage message) {
        if (!properties.isEnabled()) {
            log.debug("[NotifyCenter][Mail] disabled, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        if (properties.getFrom() == null || properties.getFrom().isEmpty()) {
            log.warn("[NotifyCenter][Mail] from is empty, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(properties.getFrom());

            // 优先从 channelConfig 中取收件人
            List<String> toList = null;
            Map<String, Object> channelConfig = message.getChannelConfig();
            if (channelConfig != null && channelConfig.get("to") instanceof List) {
                //noinspection unchecked
                toList = (List<String>) channelConfig.get("to");
            }

            if (toList == null || toList.isEmpty()) {
                toList = properties.getTo();
            }

            if (toList == null || toList.isEmpty()) {
                log.warn("[NotifyCenter][Mail] no receivers found, skip. notifyKey={}", message.getNotifyKey());
                return;
            }

            mail.setTo(toList.toArray(new String[0]));

            String subject = message.getTitle() != null ? message.getTitle() : "[NotifyCenter] " + message.getNotifyKey();
            mail.setSubject(subject);

            // 简单处理：模板格式先不管，内容统一用 text
            mail.setText(message.getContent() != null ? message.getContent() : "");

            mailSender.send(mail);
            log.debug("[NotifyCenter][Mail] send success. notifyKey={}", message.getNotifyKey());
        } catch (Exception e) {
            log.error("[NotifyCenter][Mail] send error. notifyKey={}", message.getNotifyKey(), e);
            throw e;
        }
    }
}
