package com.gtbabc.notifycenter.core.provider;

import com.gtbabc.notifycenter.core.model.NotifyRule;

public interface NotifyRuleProvider {

    NotifyRule getRule(String notifyKey);
}
