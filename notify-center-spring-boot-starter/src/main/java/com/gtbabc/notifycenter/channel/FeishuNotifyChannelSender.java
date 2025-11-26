package com.gtbabc.notifycenter.channel;

import com.gtbabc.notifycenter.channel.config.FeishuNotifyProperties;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 基于飞书自定义机器人 Webhook 的 Sender 实现
 */
@Slf4j
public class FeishuNotifyChannelSender implements NotifyChannelSender {

    private final FeishuNotifyProperties properties;
    private final RestTemplate restTemplate;

    public FeishuNotifyChannelSender(FeishuNotifyProperties properties, RestTemplate restTemplate) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate must not be null");
    }

    /**
     * 返回当前发送器对应的渠道类型
     */
    @Override
    public NotifyChannelType getChannelType() {
        return NotifyChannelType.FEI_SHU;
    }

    @Override
    public void send(NotifyMessage message) {
        if (!properties.isEnabled()) {
            log.debug("[NotifyCenter][Feishu] disabled, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        if (properties.getWebhookUrl() == null || properties.getWebhookUrl().isEmpty()) {
            log.warn("[NotifyCenter][Feishu] webhookUrl is empty, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            if (message.getTitle() != null && !message.getTitle().isEmpty()) {
                sb.append("**").append(message.getTitle()).append("**\n\n");
            }
            if (message.getContent() != null) {
                sb.append(message.getContent());
            }
            String text = sb.toString();

            Map<String, Object> content = new HashMap<>();
            content.put("text", text);

            Map<String, Object> body = new HashMap<>();
            body.put("msg_type", "text");
            body.put("content", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String resp = restTemplate.postForObject(properties.getWebhookUrl(), entity, String.class);
            log.debug("[NotifyCenter][Feishu] send success. notifyKey={}, resp={}", message.getNotifyKey(), resp);
        } catch (Exception e) {
            log.error("[NotifyCenter][Feishu] send error. notifyKey={}", message.getNotifyKey(), e);
            throw e;
        }
    }
}
