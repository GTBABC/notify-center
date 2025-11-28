package com.gtbabc.notifycentersamplespringboot.controller;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NotifySampleController {

    @Autowired
    private NotifyClient notifyClient;

    @GetMapping("/test-notify")
    public String testNotify() {
        Map<String, Object> params = new HashMap<>();
        params.put("title", "定时任务异常告警");
        params.put("jobGroup", "测试");
        params.put("taskId", "测试");
        params.put("taskDesc", "测试");
        params.put("alarmTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("alarmTitle", "调度失败");
        params.put("alarmContent", "测试");
        params.put("detail", "http://127.0.0.1:9040/xxl-job-admin//joblog");
        notifyClient.notify("order.timeout", params);
        return "ok";
    }
}
