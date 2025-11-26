package com.gtbabc.notifycenter.provider;

import com.gtbabc.notifycenter.core.constant.ChannelRule;
import com.gtbabc.notifycenter.core.constant.NotifyChannelType;
import com.gtbabc.notifycenter.core.constant.NotifyLevel;
import com.gtbabc.notifycenter.core.constant.NotifyRule;
import com.gtbabc.notifycenter.core.provider.NotifyRuleProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 classpath:/notify/rules/*.yml 加载通知规则
 *
 * 约定 YAML 结构示例：
 *
 * order.timeout:
 *   level: ALERT
 *   channels:
 *     - channelType: FEI_SHU
 *       templateId: order_timeout_feishu
 *       enabled: true
 *       extraConfig:
 *         to:
 *           - dev1@example.com
 *           - dev2@example.com
 */
@Slf4j
public class YamlNotifyRuleProvider implements NotifyRuleProvider {

    private static final String RULE_LOCATION_PATTERN = "classpath*:notify/rules/*.yml";

    private final Map<String, NotifyRule> ruleCache = new HashMap<>();

    public YamlNotifyRuleProvider() {
        loadAllRules();
    }

    @Override
    public NotifyRule getRule(String notifyKey) {
        return ruleCache.get(notifyKey);
    }

    @SuppressWarnings("unchecked")
    private void loadAllRules() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(RULE_LOCATION_PATTERN);

            if (resources.length == 0) {
                log.warn("[NotifyCenter] no rule yaml found by pattern: {}", RULE_LOCATION_PATTERN);
            }

            Yaml yaml = new Yaml();
            int fileCount = 0;

            for (Resource resource : resources) {
                fileCount++;
                try (InputStream in = resource.getInputStream()) {
                    Map<String, Object> yamlData = yaml.load(in);
                    if (yamlData == null || yamlData.isEmpty()) {
                        log.warn("[NotifyCenter] rule yaml {} is empty, skip.", resource.getFilename());
                        continue;
                    }

                    for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
                        String notifyKey = entry.getKey();
                        Object value = entry.getValue();
                        if (!(value instanceof Map)) {
                            log.warn("[NotifyCenter] rule {} in {} is not a map, skip.",
                                    notifyKey, resource.getFilename());
                            continue;
                        }

                        Map<String, Object> ruleData = (Map<String, Object>) value;
                        NotifyRule rule = parseRule(notifyKey, ruleData, resource.getFilename());
                        if (rule != null) {
                            ruleCache.put(notifyKey, rule);
                        }
                    }

                    log.info("[NotifyCenter] loaded rules from {}", resource.getFilename());
                }
            }

            log.info("[NotifyCenter] total {} rules loaded from {} file(s).", ruleCache.size(), fileCount);
        } catch (Exception e) {
            log.error("[NotifyCenter] load rules error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private NotifyRule parseRule(String notifyKey, Map<String, Object> ruleData, String fileName) {

        NotifyRule rule = new NotifyRule();

        // level
        Object levelVal = ruleData.get("level");
        NotifyLevel level = NotifyLevel.INFO;
        if (levelVal != null) {
            try {
                level = NotifyLevel.valueOf(levelVal.toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("[NotifyCenter] invalid level {} for rule {} in {}, use INFO", levelVal, notifyKey, fileName);
            }
        }
        rule.setLevel(level);

        // channels
        Object channelsVal = ruleData.get("channels");
        if (!(channelsVal instanceof List)) {
            log.warn("[NotifyCenter] rule {} in {} has no valid 'channels', skip.", notifyKey, fileName);
            return null;
        }

        List<Map<String, Object>> channelList = (List<Map<String, Object>>) channelsVal;
        List<ChannelRule> channels = new ArrayList<>();

        for (Map<String, Object> chData : channelList) {
            ChannelRule chRule = parseChannelRule(notifyKey, chData, fileName);
            if (chRule != null) {
                channels.add(chRule);
            }
        }

        if (channels.isEmpty()) {
            log.warn("[NotifyCenter] rule {} in {} has no valid channels after parse, skip.", notifyKey, fileName);
            return null;
        }

        rule.setChannels(channels);
        return rule;
    }

    @SuppressWarnings("unchecked")
    private ChannelRule parseChannelRule(String notifyKey, Map<String, Object> chData, String fileName) {

        ChannelRule ch = new ChannelRule();

        // channelType
        Object typeVal = chData.get("channelType");
        if (typeVal == null) {
            log.warn("[NotifyCenter] channel in rule {} of {} has no channelType, skip.", notifyKey, fileName);
            return null;
        }
        try {
            NotifyChannelType type = NotifyChannelType.valueOf(typeVal.toString().toUpperCase());
            ch.setChannelType(type);
        } catch (IllegalArgumentException e) {
            log.warn("[NotifyCenter] invalid channelType {} in rule {} of {}, skip.", typeVal, notifyKey, fileName);
            return null;
        }

        // templateId
        Object tplId = chData.get("templateId");
        if (tplId == null) {
            log.warn("[NotifyCenter] channel {} of rule {} in {} has no templateId, skip.", ch.getChannelType(), notifyKey, fileName);
            return null;
        }
        ch.setTemplateId(tplId.toString());

        // enabled（默认为 true）
        Object enabledVal = chData.get("enabled");
        ch.setEnabled(enabledVal == null || Boolean.parseBoolean(enabledVal.toString()));

        // extraConfig （任意 map）
        Object extra = chData.get("extraConfig");
        if (extra instanceof Map) {
            ch.setExtraConfig((Map<String, Object>) extra);
        }

        return ch;
    }
}
