package com.gtbabc.notifycenter.core.provider.impl;

import com.gtbabc.notifycenter.core.constant.NotifyRule;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;

import java.util.Map;
import java.util.Objects;

public class InMemoryNotifyRuleProvider implements NotifyRuleProvider {

    private final Map<String, NotifyRule> rules;

    public InMemoryNotifyRuleProvider(Map<String, NotifyRule> rules) {
        this.rules = Objects.requireNonNull(rules, "rules must not be null");
    }

    @Override
    public NotifyRule getRule(String notifyKey) {
        return rules.get(notifyKey);
    }
}
