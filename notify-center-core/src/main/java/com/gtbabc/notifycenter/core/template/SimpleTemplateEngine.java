package com.gtbabc.notifycenter.core.template;

import java.util.Map;

public class SimpleTemplateEngine implements TemplateEngine {

    @Override
    public String render(String template, Map<String, Object> params) {
        if (template == null || params == null || params.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
