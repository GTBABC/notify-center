package com.gtbabc.notifycenter.config.rule;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定 notify.rules.* 的配置。
 */
@ConfigurationProperties(prefix = "notify.rules")
@Data
public class NotifyRuleProperties {
    /**
     * key = notifyKey（例如 order.timeout）
     */
    private Map<String, RuleConfig> rules = new HashMap<>();
}
