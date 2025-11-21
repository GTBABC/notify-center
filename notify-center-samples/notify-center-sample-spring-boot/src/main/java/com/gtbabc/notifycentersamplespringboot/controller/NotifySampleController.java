package com.gtbabc.notifycentersamplespringboot.controller;

import com.gtbabc.notifycenter.core.api.NotifyClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotifySampleController {

    private final NotifyClient notifyClient;

    public NotifySampleController(NotifyClient notifyClient) {
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
