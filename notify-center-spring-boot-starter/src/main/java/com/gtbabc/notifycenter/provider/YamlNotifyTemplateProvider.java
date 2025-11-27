package com.gtbabc.notifycenter.provider;

import com.gtbabc.notifycenter.core.constant.NotifyTemplate;
import com.gtbabc.notifycenter.core.constant.TemplateFormat;
import com.gtbabc.notifycenter.core.provider.NotifyTemplateProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 从 classpath:/notify/templates/*.yml 加载模板定义
 * 约定 YAML 结构：
 * tpl_order_timeout:
 * titleTemplate: "订单超时：${orderId}"
 * contentTemplate: "订单 ${orderId} 已超时 ${timeoutMinutes} 分钟"
 * format: MARKDOWN
 */
@Slf4j
public class YamlNotifyTemplateProvider implements NotifyTemplateProvider {

    private static final String TEMPLATE_LOCATION_PATTERN = "classpath*:notify/templates/*.yml";

    /**
     * 缓存所有模板，key = templateId
     */
    private final Map<String, NotifyTemplate> templateCache = new HashMap<>();

    public YamlNotifyTemplateProvider() {
        loadAllTemplates();
    }

    @Override
    public NotifyTemplate getTemplate(String templateId) {
        return templateCache.get(templateId);
    }

    @SuppressWarnings("unchecked")
    private void loadAllTemplates() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(TEMPLATE_LOCATION_PATTERN);

            if (resources.length == 0) {
                log.warn("[NotifyCenter] no template yaml found by pattern: {}", TEMPLATE_LOCATION_PATTERN);
            }

            Yaml yaml = new Yaml();
            int fileCount = 0;

            for (Resource resource : resources) {
                fileCount++;

                try (InputStream in = resource.getInputStream()) {
                    Map<String, Object> yamlData = yaml.load(in);

                    if (yamlData == null || yamlData.isEmpty()) {
                        log.warn("[NotifyCenter] template yaml {} is empty.", resource.getFilename());
                        continue;
                    }

                    for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
                        String templateId = entry.getKey();
                        Object value = entry.getValue();

                        if (!(value instanceof Map)) {
                            log.warn("[NotifyCenter] template {} in {} is not a map, skip.",
                                    templateId, resource.getFilename());
                            continue;
                        }

                        Map<String, Object> tplData = (Map<String, Object>) value;

                        NotifyTemplate tpl = parseTemplate(templateId, tplData, resource.getFilename());
                        if (tpl != null) {
                            templateCache.put(templateId, tpl);
                        }
                    }

                    log.info("[NotifyCenter] loaded templates from {}", resource.getFilename());
                }
            }

            log.info("[NotifyCenter] total {} templates loaded from {} template file(s).",
                    templateCache.size(), fileCount);

        } catch (Exception e) {
            log.error("[NotifyCenter] load templates error", e);
        }
    }

    private NotifyTemplate parseTemplate(String templateId,
                                         Map<String, Object> tplData,
                                         String fileName) {

        NotifyTemplate tpl = new NotifyTemplate();
        tpl.setTemplateId(templateId);

        // titleTemplate
        Object title = tplData.get("titleTemplate");
        if (title == null) {
            log.warn("[NotifyCenter] template {} in {} has no titleTemplate, skip.", templateId, fileName);
            return null;
        }
        tpl.setTitleTemplate(title.toString());

        // contentTemplate
        Object content = tplData.get("contentTemplate");
        if (content == null) {
            log.warn("[NotifyCenter] template {} in {} has no contentTemplate, skip.", templateId, fileName);
            return null;
        }
        tpl.setContentTemplate(content);

        // format
        Object fmtObj = tplData.get("format");
        TemplateFormat fmt = TemplateFormat.TEXT;
        if (fmtObj != null) {
            try {
                fmt = TemplateFormat.valueOf(fmtObj.toString().toUpperCase());
            } catch (Exception e) {
                log.warn("[NotifyCenter] invalid format {} for template {}, use TEXT", fmtObj, templateId);
            }
        }
        tpl.setFormat(fmt);

        return tpl;
    }
}