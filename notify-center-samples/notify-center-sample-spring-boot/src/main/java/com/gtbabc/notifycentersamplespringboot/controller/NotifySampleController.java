package com.gtbabc.notifycentersamplespringboot.controller;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotifySampleController {

    @Autowired
    private NotifyClient notifyClient;

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
