package com.gtbabc.notifycenter.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtbabc.notifycenter.channel.config.DingTalkNotifyProperties;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import com.gtbabc.notifycenter.core.constant.TemplateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DingTalkNotifyChannelSender implements NotifyChannelSender {

    private final DingTalkNotifyProperties properties;

    private final RestTemplate restTemplate;

    public DingTalkNotifyChannelSender(DingTalkNotifyProperties properties, RestTemplate restTemplate) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate must not be null");
    }

    @Override
    public String getChannelType() {
        return NotifyChannelType.DING_TALK.name();
    }

    @Override
    public void send(NotifyMessage message) {
        if (!properties.isEnabled()) {
            log.debug("[NotifyCenter][DingTalk] disabled, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        String url = properties.getWebhookUrl();
        if (url == null || url.isEmpty()) {
            log.warn("[NotifyCenter][DingTalk] webhookUrl is empty, skip. notifyKey={}", message.getNotifyKey());
            return;
        }

        if (properties.getSecret() != null && !properties.getSecret().isEmpty()) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                url = url + "&timestamp=" + currentTimeMillis + "&sign=" + generateSignature(currentTimeMillis, properties.getSecret());
            } catch (Exception e) {
                log.error("[NotifyCenter][DingTalk] generateSignature error. notifyKey={}", message.getNotifyKey(), e);
                return;
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = buildBodyByFormat(message);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String resp = restTemplate.postForObject(url, entity, String.class);
            // 校验响应
            if (resp == null || resp.isEmpty()) {
                log.error("[NotifyCenter] Empty response from DingTalk webhook, notifyKey={}", message.getNotifyKey());
                return;
            }

            if (isValidDingTalkResponse(resp)) {
                log.info("[NotifyCenter] Successfully sent notification to DingTalk, notifyKey={}", message.getNotifyKey());
            }
        } catch (Exception e) {
            log.error("[NotifyCenter][DingTalk] send error. notifyKey={}", message.getNotifyKey(), e);
            throw e;
        }
    }

    private Map<String, Object> buildBodyByFormat(NotifyMessage message) {
        Map<String, Object> body = new HashMap<>();
        if (message.getFormat() == TemplateFormat.MARKDOWN) {
            body.put("msgtype", "markdown");
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", message.getTitle());
            markdown.put("text", message.getContent());
            body.put("markdown", markdown);
        } else {
            body.put("msgtype", "text");
            body.put("text", message.getContent().toString());
        }
        Map<String, Object> channelConfig = message.getChannelConfig();
        if (channelConfig != null && !channelConfig.isEmpty()) {
            Object at = channelConfig.get("at");
            if (at != null) {
                body.put("at", at);
            }
        }
        return body;
    }

    private boolean isValidDingTalkResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 使用 Jackson 解析 JSON 响应
            JsonNode jsonResponse = objectMapper.readTree(response);
            int code = jsonResponse.path("errcode").asInt();

            // 判断是否成功 (code 为 0)
            if (code == 0) {
                return true;
            }

            // 如果是失败的响应，记录失败信息
            String errorMsg = jsonResponse.path("errmsg").asText("Unknown error");
            log.error("[NotifyCenter] DingTalk request failed: code={}, msg={}", code, errorMsg);
            return false;
        } catch (Exception e) {
            log.error("[NotifyCenter] Error parsing DingTalk response: ", e);
            return false;
        }
    }

    /**
     * 生成签名
     */
    private String generateSignature(long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), StandardCharsets.UTF_8);
    }
}
