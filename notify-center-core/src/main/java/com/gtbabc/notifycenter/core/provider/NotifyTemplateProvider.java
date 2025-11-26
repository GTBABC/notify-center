package com.gtbabc.notifycenter.core.provider;

import com.gtbabc.notifycenter.core.constant.NotifyTemplate;

public interface NotifyTemplateProvider {

    NotifyTemplate getTemplate(String templateId);
}
