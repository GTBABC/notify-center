package com.gtbabc.notifycenter.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtbabc.notifycenter.channel.config.FeishuNotifyProperties;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import com.gtbabc.notifycenter.core.constant.TemplateFormat;
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
            log.debug("[NotifyCenter][FeiShu] disabled, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        if (properties.getWebhookUrl() == null || properties.getWebhookUrl().isEmpty()) {
            log.warn("[NotifyCenter][FeiShu] webhookUrl is empty, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = buildBodyByFormat(message);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String resp = restTemplate.postForObject(properties.getWebhookUrl(), entity, String.class);
            // 校验响应
            if (resp == null || resp.isEmpty()) {
                log.error("[NotifyCenter] Empty response from FeiShu webhook, notifyKey={}", message.getNotifyKey());
                return;
            }

            // 校验返回的响应是否符合飞书成功的标准
            if (isValidFeiShuResponse(resp)) {
                log.info("[NotifyCenter] Successfully sent notification to FeiShu, notifyKey={}", message.getNotifyKey());
            }
        } catch (Exception e) {
            log.error("[NotifyCenter][FeiShu] send error. notifyKey={}", message.getNotifyKey(), e);
            throw e;
        }
    }

    private Map<String, Object> buildBodyByFormat(NotifyMessage message) {
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        body.put("content", content);
        if (message.getFormat() == TemplateFormat.MARKDOWN) {
            body.put("msg_type", "markdown");
            content.put("text", message.getContent());
        } else if (message.getFormat() == TemplateFormat.POST) {
            body.put("msg_type", "post");
            Map<String, Object> post = new HashMap<>();
            content.put("post", post);
            Map<String, Object> zhCn = new HashMap<>();
            post.put("zh_cn", zhCn);
            zhCn.put("title", message.getTitle());
            zhCn.put("content", message.getContent());
        } else {
            body.put("msg_type", "text");
            content.put("text", message.getContent());
        }
        return body;
    }

    private boolean isValidFeiShuResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 使用 Jackson 解析 JSON 响应
            JsonNode jsonResponse = objectMapper.readTree(response);
            int code = jsonResponse.path("code").asInt();

            // 判断是否成功 (code 为 0)
            if (code == 0) {
                return true;
            }

            // 如果是失败的响应，记录失败信息
            String errorMsg = jsonResponse.path("msg").asText("Unknown error");
            log.error("[NotifyCenter] FeiShu request failed: code={}, msg={}", code, errorMsg);
            return false;
        } catch (Exception e) {
            log.error("[NotifyCenter] Error parsing FeiShu response: ", e);
            return false;
        }
    }
}
