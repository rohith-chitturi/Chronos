package com.example.simulatorapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

@RestController
@RequestMapping("/simulate")
@RequiredArgsConstructor
@Slf4j
public class SimulatorController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/ecommerce-flow")
    public String triggerEcommerceFlow(@RequestParam(required = false) String failAt) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        UUID timelineId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        String payload = String.format("""
            {
                "eventId": "%s",
                "timelineId": "%s",
                "serviceName": "simulator-api",
                "eventType": "START_SIMULATION",
                "aggregateType": "ORDER",
                "aggregateId": "%s",
                "payload": { "failAt": "%s" },
                "timestamp": "%s"
            }
            """, eventId, timelineId, orderId, failAt != null ? failAt : "none", Instant.now().toString());

        // We trigger the flow by sending a command to the order-service
        kafkaTemplate.send("order-commands", payload);
        log.info("Triggered ecommerce flow for order {} on timeline {}", orderId, timelineId);

        return "Simulation triggered. Timeline ID: " + timelineId + " Order ID: " + orderId;
    }
}
