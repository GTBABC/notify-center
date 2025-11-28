package com.gtbabc.notifycenter.core.template.impl;

import com.gtbabc.notifycenter.core.template.NotifyTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleNotifyTemplateEngine implements NotifyTemplateEngine {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    @Override
    public String render(String template, Map<String, Object> params) {
        if (template == null || params == null || params.isEmpty()) {
            return template;
        }
        StringBuilder result = new StringBuilder();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        int lastIndex = 0;

        // 遍历模板中的所有占位符
        while (matcher.find()) {
            // 添加占位符之前的文本部分
            result.append(template, lastIndex, matcher.start());

            // 获取占位符的名称
            String placeholder = matcher.group(1);

            // 获取对应的参数值
            String value = params.getOrDefault(placeholder, "").toString();

            // 将占位符替换为对应的值
            result.append(value);

            lastIndex = matcher.end();
        }

        // 将剩余的模板部分添加到结果中
        result.append(template.substring(lastIndex));

        return result.toString();
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
