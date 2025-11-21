package com.gtbabc.notifycenter.core.template;

import java.util.Map;

public interface TemplateEngine {

    String render(String template, Map<String, Object> params);
}
