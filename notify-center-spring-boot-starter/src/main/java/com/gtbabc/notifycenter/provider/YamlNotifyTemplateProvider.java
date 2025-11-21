package com.gtbabc.notifycenter.provider;

import com.gtbabc.notifycenter.config.template.NotifyTemplateProperties;
import com.gtbabc.notifycenter.core.model.NotifyTemplate;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;

public class YamlNotifyTemplateProvider implements NotifyTemplateProvider {

    private final NotifyTemplateProperties properties;

    public YamlNotifyTemplateProvider(NotifyTemplateProperties properties) {
        this.properties = properties;
    }

    @Override
    public NotifyTemplate getTemplate(String templateId) {
        if (templateId == null) {
            return null;
        }
        NotifyTemplateProperties.TemplateConfig cfg = properties.getTemplates().get(templateId);
        if (cfg == null) {
            return null;
        }

        NotifyTemplate t = new NotifyTemplate();
        t.setTemplateId(templateId);
        t.setChannelType(cfg.getChannel());
        t.setFormat(cfg.getFormat());
        t.setTitleTemplate(cfg.getTitle());
        t.setContentTemplate(cfg.getContent());
        return t;
    }
}
