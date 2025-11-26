package com.gtbabc.notifycenter.core.provider.impl;

import com.gtbabc.notifycenter.core.constant.NotifyTemplate;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;

import java.util.Map;
import java.util.Objects;

public class InMemoryNotifyTemplateProvider implements NotifyTemplateProvider {

    private final Map<String, NotifyTemplate> templates;

    public InMemoryNotifyTemplateProvider(Map<String, NotifyTemplate> templates) {
        this.templates = Objects.requireNonNull(templates, "templates must not be null");
    }

    @Override
    public NotifyTemplate getTemplate(String templateId) {
        return templates.get(templateId);
    }
}
