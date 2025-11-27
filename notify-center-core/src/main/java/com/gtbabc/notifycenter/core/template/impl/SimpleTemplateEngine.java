package com.gtbabc.notifycenter.core.template.impl;

import com.gtbabc.notifycenter.core.template.TemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public Object renderObject(Object obj, Map<String, Object> params) {
        if (obj instanceof String str) {
            return render(str, params);
        }
        if (obj instanceof Map) {
            Map<String, Object> newMap = new HashMap<>();
            ((Map<?, ?>) obj).forEach((k, v) -> {
                newMap.put(k.toString(), renderObject(v, params));
            });
            return newMap;
        }
        if (obj instanceof List) {
            List<Object> list = new ArrayList<>();
            for (Object item : (List<?>) obj) {
                list.add(renderObject(item, params));
            }
            return list;
        }
        return obj;
    }

}
