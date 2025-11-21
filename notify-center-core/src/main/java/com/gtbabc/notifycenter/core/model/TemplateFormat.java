package com.gtbabc.notifycenter.core.model;

/**
 * 通知模板格式类型枚举
 * `TemplateFormat` 用于定义通知模板的格式。不同的模板格式决定了通知内容的展示形式，
 * 如普通文本、Markdown 或 HTML 格式。
 */
public enum TemplateFormat {

    /**
     * 普通文本格式，最基本的文本展示方式
     * 适用于简单的通知内容，支持占位符替换
     */
    TEXT,

    /**
     * Markdown 格式，支持格式化文本，如粗体、斜体、列表等
     * 适用于需要格式化显示的通知内容，常用于钉钉、飞书等支持 Markdown 的平台
     */
    MARKDOWN,

    /**
     * HTML 格式，支持丰富的网页样式和排版
     * 适用于需要更复杂排版、样式的通知内容，常用于电子邮件等支持 HTML 的渠道
     */
    HTML
}
