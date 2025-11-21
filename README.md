# notify-center

`notify-center` 是一个用于统一「消息通知 / 告警」的组件，支持按规则在多种渠道（如钉钉、飞书、邮件等）发送模板化通知。

项目拆分为两层：

- **notify-center-core**：核心通知引擎（纯 Java，独立于 Spring）
- **notify-center-spring-boot-starter**：Spring Boot 集成与默认实现（YAML 配置 + 自动装配）

---

## 1. 模块说明

### 1.1 notify-center-core

核心职责：

- 定义通知领域模型：
    - `NotifyRule` / `ChannelRule`
    - `NotifyTemplate`
    - `NotifyMessage`
    - `NotifyLevel`
    - `NotifyChannelType`
- 定义扩展接口：
    - `NotifyClient`：对业务暴露的通知入口
    - `NotifyRuleProvider`：规则提供者（notifyKey → NotifyRule）
    - `NotifyTemplateProvider`：模板提供者（templateId → NotifyTemplate）
    - `NotifyChannelSender`：渠道发送适配器
    - `TemplateEngine`：模板引擎
- 提供默认实现：
    - `DefaultNotifyClient`：完成「规则 → 模板 → 渲染 → 调用 Sender」的完整流程
    - `SimpleTemplateEngine`：基于 `${key}` 的简单字符串替换

> `notify-center-core` 不依赖 Spring，仅依赖 `slf4j-api`，可在任何 Java 项目中单独使用。

---

### 1.2 notify-center-spring-boot-starter

核心职责：

- 通过 `@ConfigurationProperties` 从配置中绑定：
    - `notify.rules` → 通知规则
    - `notify.templates` → 通知模板
- 提供 YAML 实现的 Provider：
    - `YamlNotifyRuleProvider`
    - `YamlNotifyTemplateProvider`
- 提供默认 Sender 示例（如钉钉 Sender：当前仅输出日志）
- 提供自动装配：
    - `NotifyAutoConfiguration` 自动注册：
        - `TemplateEngine`（默认 `SimpleTemplateEngine`）
        - `NotifyRuleProvider` / `NotifyTemplateProvider`
        - 默认 `NotifyChannelSender` 实例
        - 组装为 `DefaultNotifyClient`，暴露 `NotifyClient` Bean

> 所有默认 Bean 均使用 `@ConditionalOnMissingBean`，业务可以通过自定义 Bean 覆盖默认实现（例如使用 DB 规则、Nacos 配置、自定义 Sender 等）。

---

## 2. 基本使用方式（在业务服务中）

### 2.1 引入依赖

在业务服务的 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>com.gtbabc</groupId>
    <artifactId>notify-center-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2.2 配置规则与模板

在业务服务的 `application.yml` 中配置：

```yaml
notify:
  rules:
    order.timeout:
      level: ALERT
      channels:
        - channel: DING_TALK
          templateId: tpl_order_timeout_dingtalk

  templates:
    tpl_order_timeout_dingtalk:
      channel: DING_TALK
      format: MARKDOWN
      title: "【订单超时】订单${orderId}"
      content: |
        订单 ${orderId} 已超时 ${timeoutMinutes} 分钟
        用户：${userId}
```

### 2.3 代码中使用

```java
import com.gtbabc.notifycenter.core.api.NotifyClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {

    private final NotifyClient notifyClient;

    public DemoController(NotifyClient notifyClient) {
        this.notifyClient = notifyClient;
    }

    @GetMapping("/test-notify")
    public String testNotify() {
        notifyClient.notify("order.timeout", Map.of(
                "orderId", 12345,
                "userId",  67890,
                "timeoutMinutes", 30
        ));
        return "ok";
    }
}
```

当前默认的 `DingTalkNotifyChannelSender` 仅将要发送的内容输出到日志，后续可以在该类中接入真实钉钉机器人调用。

---

## 3. 扩展点

- 自定义规则来源：
    - 实现 `NotifyRuleProvider` / `NotifyTemplateProvider`，例如从 DB / Nacos 读取，并在业务中声明为 Bean，即可覆盖默认 YAML Provider。
- 自定义渠道 Sender：
    - 实现 `NotifyChannelSender`（例如飞书、邮件、企业微信等），并在 Spring 中声明为 Bean。
- 自定义模板引擎：
    - 实现 `TemplateEngine`，如接入 Freemarker/Thymeleaf 等模板引擎。

通过这些扩展点，可以在不修改 `notify-center-core` 的前提下应对复杂的通知/告警需求。
