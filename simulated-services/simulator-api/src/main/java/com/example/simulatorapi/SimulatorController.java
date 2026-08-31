package com.example.simulatorapi;

import com.example.simulatorapi.fault.FaultRule;
import com.example.simulatorapi.fault.FaultStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/simulate")
@RequiredArgsConstructor
@Slf4j
public class SimulatorController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        kafkaTemplate.send("order-commands", payload);
        log.info("Triggered ecommerce flow for order {} on timeline {}", orderId, timelineId);

        return "Simulation triggered. Timeline ID: " + timelineId + " Order ID: " + orderId;
    }

    @PostMapping("/faults")
    public FaultRule injectFault(@RequestBody FaultRule rule) {
        try {
            if (rule.getRuleId() == null) {
                rule.setRuleId("FAULT-" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (rule.getStatus() == null) {
                rule.setStatus(FaultStatus.PENDING);
            }
            
            String ruleJson = objectMapper.writeValueAsString(rule);
            
            // Broadcast to simulated services
            kafkaTemplate.send("fault-control", ruleJson);
            
            // Emit FAULT_INJECTED to timeline (using order-events to reach Chronos)
            ObjectNode eventNode = objectMapper.createObjectNode();
            eventNode.put("eventId", UUID.randomUUID().toString());
            eventNode.put("timelineId", "MAIN");
            eventNode.put("serviceName", "simulator-api");
            eventNode.put("eventType", "FAULT_INJECTED");
            eventNode.put("aggregateType", "SYSTEM");
            eventNode.put("aggregateId", rule.getRuleId());
            eventNode.put("timestamp", Instant.now().toString());
            eventNode.set("payload", objectMapper.valueToTree(rule));

            kafkaTemplate.send("order-events", objectMapper.writeValueAsString(eventNode));
            
            log.info("Injected fault rule: {}", rule.getRuleId());
            
        } catch (Exception e) {
            log.error("Failed to inject fault", e);
        }
        return rule;
    }
}
