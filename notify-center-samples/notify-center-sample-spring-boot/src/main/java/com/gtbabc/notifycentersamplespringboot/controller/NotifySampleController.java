package com.gtbabc.notifycentersamplespringboot.controller;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NotifySampleController {

    @Autowired
    private NotifyClient notifyClient;

    @GetMapping("/test-notify")
    public String testNotify(@RequestParam("orderId") Long orderId, @RequestParam("userId") Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("userId", userId);
        params.put("timeoutMinutes", 30);

        // 这里的 "order.timeout" 对应 YAML 规则里的 notifyKey
        notifyClient.notify("order.timeout", params);
        return "success";
    }
}
