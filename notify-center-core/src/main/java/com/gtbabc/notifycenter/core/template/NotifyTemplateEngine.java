package com.gtbabc.notifycenter.core.template;

import java.util.Map;

public interface NotifyTemplateEngine {

    String render(String template, Map<String, Object> params);

    Object renderObject(Object template, Map<String, Object> params);
}
