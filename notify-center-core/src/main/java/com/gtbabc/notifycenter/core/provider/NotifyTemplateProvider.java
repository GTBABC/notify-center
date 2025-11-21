package com.gtbabc.notifycenter.core.provider;

import com.gtbabc.notifycenter.core.model.NotifyTemplate;

public interface NotifyTemplateProvider {

    NotifyTemplate getTemplate(String templateId);
}
