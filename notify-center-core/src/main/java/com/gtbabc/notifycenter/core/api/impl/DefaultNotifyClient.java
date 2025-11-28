package com.gtbabc.notifycenter.core.api.impl;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import com.gtbabc.notifycenter.core.api.NotifyInterceptor;
import com.gtbabc.notifycenter.core.api.RetryPolicy;
import com.gtbabc.notifycenter.core.api.model.ChannelSendResult;
import com.gtbabc.notifycenter.core.api.model.NotifyContext;
import com.gtbabc.notifycenter.core.api.model.NotifyResult;
import com.gtbabc.notifycenter.core.channel.NotifyChannelSender;
import com.gtbabc.notifycenter.core.constant.ChannelRule;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;
import com.gtbabc.notifycenter.core.constant.NotifyMessage;
import com.gtbabc.notifycenter.core.constant.NotifyRule;
import com.gtbabc.notifycenter.core.constant.NotifyTemplate;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import com.gtbabc.notifycenter.core.template.NotifyTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 默认通知客户端实现
 * - 同步执行
 * - 支持拦截器
 * - 支持可插拔重试策略
 */
@Slf4j
public class DefaultNotifyClient implements NotifyClient {

    private final NotifyRuleProvider ruleProvider;
    private final NotifyTemplateProvider templateProvider;
    private final NotifyTemplateEngine notifyTemplateEngine;
    private final Map<NotifyChannelType, NotifyChannelSender> senderMap;
    private final List<NotifyInterceptor> interceptors;
    private final RetryPolicy retryPolicy;

    public DefaultNotifyClient(NotifyRuleProvider ruleProvider,
                               NotifyTemplateProvider templateProvider,
                               NotifyTemplateEngine notifyTemplateEngine,
                               Map<NotifyChannelType, NotifyChannelSender> senderMap) {
        this(ruleProvider, templateProvider, notifyTemplateEngine, senderMap,
                Collections.emptyList(), new NoRetryPolicy());
    }

    public DefaultNotifyClient(NotifyRuleProvider ruleProvider,
                               NotifyTemplateProvider templateProvider,
                               NotifyTemplateEngine notifyTemplateEngine,
                               Map<NotifyChannelType, NotifyChannelSender> senderMap,
                               List<NotifyInterceptor> interceptors,
                               RetryPolicy retryPolicy) {
        this.ruleProvider = Objects.requireNonNull(ruleProvider, "ruleProvider must not be null");
        this.templateProvider = Objects.requireNonNull(templateProvider, "templateProvider must not be null");
        this.notifyTemplateEngine = Objects.requireNonNull(notifyTemplateEngine, "templateEngine must not be null");
        this.senderMap = Objects.requireNonNull(senderMap, "senderMap must not be null");
        this.interceptors = interceptors != null ? interceptors : Collections.emptyList();
        this.retryPolicy = retryPolicy != null ? retryPolicy : new NoRetryPolicy();
    }

    @Override
    public NotifyResult notify(String notifyKey, Map<String, Object> params) {
        return notify(notifyKey, null, null, params);
    }

    @Override
    public NotifyResult notify(String notifyKey, NotifyLevel level, Map<String, Object> params) {
        return notify(notifyKey, null, level, params);
    }

    @Override
    public NotifyResult notify(String notifyKey, String prefixTemplateId, Map<String, Object> params) {
        return notify(notifyKey, prefixTemplateId, null, params);
    }

    @Override
    public NotifyResult notify(String notifyKey, String prefixTemplateId, NotifyLevel level, Map<String, Object> params) {
        return execute(notifyKey, prefixTemplateId, level, params);
    }

    private NotifyResult execute(String notifyKey, String prefixTemplateId, NotifyLevel overrideLevel, Map<String, Object> params) {
        NotifyResult result = new NotifyResult();
        result.setNotifyKey(notifyKey);
        result.setChannelResults(new HashMap<>());

        if (notifyKey == null || notifyKey.isEmpty()) {
            log.warn("[NotifyCenter] notifyKey is empty, skip. params={}", params);
            result.setRuleFound(false);
            return result;
        }

        NotifyRule rule = ruleProvider.getRule(notifyKey);
        if (rule == null) {
            log.warn("[NotifyCenter] no rule found for notifyKey={}", notifyKey);
            result.setRuleFound(false);
            return result;
        }
        result.setRuleFound(true);

        NotifyLevel finalLevel = overrideLevel != null
                ? overrideLevel
                : (rule.getLevel() != null ? rule.getLevel() : NotifyLevel.INFO);
        result.setFinalLevel(finalLevel);

        if (rule.getChannels() == null || rule.getChannels().isEmpty()) {
            log.warn("[NotifyCenter] no channels configured for notifyKey={}", notifyKey);
            return result;
        }

        for (ChannelRule channelRule : rule.getChannels()) {

            NotifyChannelType channelType = channelRule.getChannelType();
            ChannelSendResult chResult = new ChannelSendResult();
            chResult.setChannelType(channelType);
            result.getChannelResults().put(channelType, chResult);

            // 1. 启用检查
            if (!Boolean.TRUE.equals(channelRule.isEnabled())) {
                log.debug("[NotifyCenter] channel disabled for notifyKey={}, channel={}", notifyKey, channelType);
                chResult.setSuccess(false);
                chResult.setErrorCode("CHANNEL_DISABLED");
                chResult.setErrorMessage("Channel is disabled");
                continue;
            }

            if (channelType == null) {
                log.warn("[NotifyCenter] channelType is null in channelRule, notifyKey={}", notifyKey);
                chResult.setSuccess(false);
                chResult.setErrorCode("CHANNEL_TYPE_NULL");
                chResult.setErrorMessage("channelType is null in channelRule");
                continue;
            }

            String templateId = prefixTemplateId != null && !prefixTemplateId.isEmpty() ? prefixTemplateId + "_" + channelType : null;
            NotifyTemplate template = templateProvider.getTemplate(templateId);
            if (template == null) {
                template = templateProvider.getTemplate(channelRule.getTemplateId());
            }

            if (template == null) {
                log.warn("[NotifyCenter] no template found, templateId={}, rule's templateId={}, notifyKey={}, channel={}",
                        templateId, channelRule.getTemplateId(), notifyKey, channelType);
                chResult.setSuccess(false);
                chResult.setErrorCode("TEMPLATE_NOT_FOUND");
                chResult.setErrorMessage("template not found: " + templateId);
                continue;
            }

            // 组装基本 NotifyContext（后面每个 attempt 会更新消息和 attempt 字段）
            NotifyContext ctx = new NotifyContext();
            ctx.setNotifyKey(notifyKey);
            ctx.setParams(params);
            ctx.setRule(rule);
            ctx.setTemplate(template);
            ctx.setChannelRule(channelRule);
            ctx.setChannelType(channelType);
            ctx.setLevel(finalLevel);

            // 2. 渲染 + 发送 + 重试
            int attempt = 0;
            Exception lastEx = null;

            while (true) {
                attempt++;
                ctx.setAttempt(attempt);

                String title;
                Object content;
                try {
                    title = notifyTemplateEngine.render(template.getTitleTemplate(), params);
                    content = notifyTemplateEngine.renderObject(template.getContentTemplate(), params);
                } catch (Exception e) {
                    // 模板渲染错误通常没必要重试
                    log.error("[NotifyCenter] render template error, templateId={}, notifyKey={}, channel={}",
                            templateId, notifyKey, channelType, e);
                    chResult.setSuccess(false);
                    chResult.setErrorCode("TEMPLATE_RENDER_ERROR");
                    chResult.setErrorMessage(e.getMessage());
                    lastEx = e;
                    break;
                }

                NotifyMessage message = new NotifyMessage();
                message.setNotifyKey(notifyKey);
                message.setChannelType(channelType);
                message.setLevel(finalLevel);
                message.setTitle(title);
                message.setContent(content);
                message.setContext(params);
                message.setFormat(template.getFormat());
                message.setChannelConfig(channelRule.getExtraConfig());
                ctx.setMessage(message);

                NotifyChannelSender sender = senderMap.get(channelType);
                if (sender == null) {
                    log.warn("[NotifyCenter] no sender found for notifyKey={}, channel={}", notifyKey, channelType);
                    chResult.setSuccess(false);
                    chResult.setErrorCode("SENDER_NOT_FOUND");
                    chResult.setErrorMessage("sender not found for channelType=" + channelType);
                    break;
                }

                // beforeSend hook
                for (NotifyInterceptor interceptor : interceptors) {
                    try {
                        interceptor.beforeSend(ctx);
                    } catch (Exception e) {
                        log.warn("[NotifyCenter] beforeSend interceptor error, ignore. notifyKey={}, channel={}",
                                notifyKey, channelType, e);
                    }
                }

                try {
                    sender.send(message);
                    chResult.setSuccess(true);
                    chResult.setErrorCode(null);
                    chResult.setErrorMessage(null);
                    lastEx = null;
                    break;
                } catch (Exception e) {
                    log.error("[NotifyCenter] send notify error, notifyKey={}, channel={}, templateId={}, attempt={}",
                            notifyKey, channelType, templateId, attempt, e);
                    chResult.setSuccess(false);
                    chResult.setErrorCode("SEND_ERROR");
                    chResult.setErrorMessage(e.getMessage());
                    lastEx = e;

                    // 判断是否还需要重试
                    if (!retryPolicy.shouldRetry(ctx, attempt, e)) {
                        break;
                    }
                    long delay = retryPolicy.nextDelayMillis(ctx, attempt, e);
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            // afterSend hook
            for (NotifyInterceptor interceptor : interceptors) {
                try {
                    interceptor.afterSend(ctx, chResult);
                } catch (Exception e) {
                    log.warn("[NotifyCenter] afterSend interceptor error, ignore. notifyKey={}, channel={}",
                            notifyKey, channelType, e);
                }
            }

            if (lastEx != null) {
                log.error(
                        "[NotifyCenter] FINAL FAILURE after {} attempts. notifyKey={}, channel={}, templateId={}, level={}, error={}",
                        ctx.getAttempt(),
                        notifyKey,
                        channelType,
                        ctx.getTemplate() != null ? ctx.getTemplate().getTemplateId() : "null",
                        ctx.getLevel(),
                        lastEx.getMessage(),
                        lastEx
                );
            }
        }

        return result;
    }
}
