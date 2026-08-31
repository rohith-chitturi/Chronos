package com.example.demo;

import com.example.demo.fault.ExecutionMode;
import com.example.demo.fault.FaultRule;
import com.example.demo.fault.FaultStatus;
import com.example.demo.fault.FaultType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventHandlerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Fault Registry
    private final Map<String, FaultRule> faultRegistry = new ConcurrentHashMap<>();

    @KafkaListener(topics = "fault-control")
    public void consumeFaultRule(String message) {
        try {
            FaultRule rule = objectMapper.readValue(message, FaultRule.class);
            if ("payment-service".equals(rule.getTargetService())) {
                rule.setStatus(FaultStatus.ACTIVE);
                faultRegistry.put(rule.getRuleId(), rule);
                log.info("Registered fault rule: {}", rule.getRuleId());
            }
        } catch (Exception e) {
            log.error("Error processing fault rule", e);
        }
    }

    @KafkaListener(topics = "order-events")
    public void consume(String message) {
        try {
            JsonNode incoming = objectMapper.readTree(message);
            log.info("Received message: {}", incoming);
            
            String failAt = incoming.path("payload").path("failAt").asText("none");
            String timelineId = incoming.path("timelineId").asText();
            String orderId = incoming.path("aggregateId").asText();
            String causationId = incoming.path("eventId").asText();
            
            String myEventType = "PAYMENT_SUCCESS";
            if ("payment-service".equals("inventory-service") && "inventory-timeout".equals(failAt)) {
                myEventType = "INVENTORY_TIMEOUT";
            }
            if ("payment-service".equals("payment-service") && "payment-failed".equals(failAt)) {
                myEventType = "PAYMENT_FAILED";
            }
            
            String incomingEvent = incoming.path("eventType").asText();
            if (incomingEvent.contains("FAILED") || incomingEvent.contains("TIMEOUT")) {
                if ("payment-service".equals("order-service")) {
                    myEventType = "ORDER_FAILED";
                } else {
                    return; 
                }
            }
            if (incomingEvent.equals("SHIPPING_CREATED") && "payment-service".equals("order-service")) {
                myEventType = "ORDER_COMPLETED";
            }
            
            // --- FAULT INTERCEPTOR START ---
            FaultRule activeFault = null;
            for (FaultRule rule : faultRegistry.values()) {
                if (rule.getStatus() == FaultStatus.ACTIVE && rule.getTargetEventType().equals(myEventType)) {
                    Random rand = new Random(rule.getSeed());
                    if (rand.nextDouble() <= rule.getProbability()) {
                        activeFault = rule;
                        break;
                    }
                }
            }

            if (activeFault != null) {
                log.info("Intercepting event {} with fault {}", myEventType, activeFault.getFaultType());
                activeFault.setStatus(FaultStatus.TRIGGERED);
                
                // Emit FAULT_TRIGGERED
                emitEvent(timelineId, orderId, causationId, "FAULT_TRIGGERED", "none", activeFault.getRuleId());

                if (activeFault.getExecutionMode() == ExecutionMode.ONE_SHOT) {
                    activeFault.setStatus(FaultStatus.EXPIRED);
                } else {
                    activeFault.setStatus(FaultStatus.ACTIVE); // Re-activate for persistent
                }

                switch (activeFault.getFaultType()) {
                    case CRASH:
                        log.warn("CRASH fault triggered. Refusing processing.");
                        return;
                    case DROP:
                        log.warn("DROP fault triggered. Dropping message silently.");
                        return;
                    case LATENCY:
                        log.warn("LATENCY fault triggered. Sleeping for {} ms", activeFault.getDurationMs());
                        Thread.sleep(activeFault.getDurationMs());
                        break;
                    case DUPLICATE:
                        log.warn("DUPLICATE fault triggered. Emitting event twice.");
                        emitEvent(timelineId, orderId, causationId, myEventType, failAt, null);
                        break;
                }
            }
            // --- FAULT INTERCEPTOR END ---

            if ("payment-service".equals("payment-service") && myEventType.equals("PAYMENT_SUCCESS")) {
                 emitEvent(timelineId, orderId, causationId, "PAYMENT_STARTED", failAt, null);
                 Thread.sleep(200);
            }
            if ("payment-service".equals("inventory-service")) {
                 emitEvent(timelineId, orderId, causationId, "INVENTORY_RESERVATION_REQUESTED", failAt, null);
                 Thread.sleep(200);
            }
            
            Thread.sleep(500);
            emitEvent(timelineId, orderId, causationId, myEventType, failAt, null);
            
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
    
    private void emitEvent(String timelineId, String aggregateId, String causationId, String eventType, String failAt, String faultRuleId) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("eventId", UUID.randomUUID().toString());
            root.put("timelineId", timelineId);
            root.put("serviceName", "payment-service");
            root.put("eventType", eventType);
            root.put("aggregateType", faultRuleId != null ? "FAULT" : "ORDER");
            root.put("aggregateId", faultRuleId != null ? faultRuleId : aggregateId);
            root.put("causationId", causationId);
            root.put("correlationId", aggregateId);
            root.put("timestamp", Instant.now().toString());
            
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("failAt", failAt);
            if (faultRuleId != null) payload.put("ruleId", faultRuleId);
            root.set("payload", payload);
            
            String outboundMessage = objectMapper.writeValueAsString(root);
            kafkaTemplate.send("payment-events", outboundMessage);
            log.info("Emitted event: {}", eventType);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
