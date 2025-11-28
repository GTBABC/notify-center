package com.gtbabc;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.api.impl.DefaultNotifyClient;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.ChannelRule;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import com.gtbabc.notifycenter.core.constant.NotifyRule;
import com.gtbabc.notifycenter.core.constant.NotifyTemplate;
import com.gtbabc.notifycenter.core.constant.TemplateFormat;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import com.gtbabc.notifycenter.core.template.NotifyTemplateEngine;
import com.gtbabc.notifycenter.core.template.impl.SimpleNotifyTemplateEngine;

import java.util.List;
import java.util.Map;

/**
 * 纯 Java 示例：不依赖 Spring，直接 new 出来用。
 */
public class CoreSampleApp {

    public static void main(String[] args) {
        // 1. 准备 Provider 和 Sender
        NotifyRuleProvider ruleProvider = new InMemoryRuleProvider();
        NotifyTemplateProvider templateProvider = new InMemoryTemplateProvider();
        NotifyTemplateEngine notifyTemplateEngine = new SimpleNotifyTemplateEngine();

        NotifyChannelSender consoleSender = new ConsoleNotifyChannelSender();

        Map<NotifyChannelType, NotifyChannelSender> senderMap = Map.of(consoleSender.getChannelType(), consoleSender);

        // 2. 组装 NotifyClient
        NotifyClient notifyClient = new DefaultNotifyClient(ruleProvider, templateProvider, notifyTemplateEngine, senderMap);

        // 3. 调用 notify
        notifyClient.notify("order.timeout", Map.of(
                "orderId", 12345,
                "userId", 67890,
                "timeoutMinutes", 30
        ));
    }

    /**
     * 内存规则：写死一条 order.timeout 规则，走 CONSOLE 渠道。
     */
    static class InMemoryRuleProvider implements NotifyRuleProvider {
        @Override
        public NotifyRule getRule(String notifyKey) {
            if (!"order.timeout".equals(notifyKey)) {
                return null;
            }
            NotifyRule rule = new NotifyRule();
            rule.setNotifyKey("order.timeout");
            rule.setLevel(NotifyLevel.ALERT);

            ChannelRule ch = new ChannelRule();
            ch.setChannelType(NotifyChannelType.DING_TALK); // 这里随便用一个枚举值
            ch.setTemplateId("tpl_order_timeout_console");
            rule.setChannels(List.of(ch));
            return rule;
        }
    }

    /**
     * 内存模板：写死一个模板。
     */
    static class InMemoryTemplateProvider implements NotifyTemplateProvider {
        @Override
        public NotifyTemplate getTemplate(String templateId) {
            if (templateId == null || templateId.isEmpty()) {
                return null;
            }
            if (!"tpl_order_timeout_console".equals(templateId)) {
                return null;
            }
            NotifyTemplate t = new NotifyTemplate();
            t.setTemplateId(templateId);
            t.setChannelType(NotifyChannelType.DING_TALK);
            t.setFormat(TemplateFormat.TEXT);
            t.setTitleTemplate("【订单超时】订单${orderId}");
            t.setContentTemplate("订单 ${orderId} 已超时 ${timeoutMinutes} 分钟，用户 ${userId}");
            return t;
        }
    }

    /**
     * 简单的控制台 Sender：把消息打印出来。
     */
    static class ConsoleNotifyChannelSender implements NotifyChannelSender {
        @Override
        public NotifyChannelType getChannelType() {
            return NotifyChannelType.DING_TALK;
        }

        @Override
        public void send(NotifyMessage message) {
            System.out.println("=== ConsoleNotifyChannelSender ===");
            System.out.println("notifyKey: " + message.getNotifyKey());
            System.out.println("channel:   " + message.getChannelType());
            System.out.println("level:     " + message.getLevel());
            System.out.println("title:     " + message.getTitle());
            System.out.println("content:   " + message.getContent());
            System.out.println("==================================");
        }
    }
}
