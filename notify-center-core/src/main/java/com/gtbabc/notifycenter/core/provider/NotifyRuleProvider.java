package com.gtbabc.notifycenter.core.provider;

import com.gtbabc.notifycenter.core.constant.NotifyRule;

public interface NotifyRuleProvider {

    NotifyRule getRule(String notifyKey);
}
