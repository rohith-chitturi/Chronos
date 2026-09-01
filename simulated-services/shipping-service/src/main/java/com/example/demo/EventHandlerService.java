package com.example.demo;

import com.example.demo.fault.ExecutionMode;
import com.example.demo.fault.FaultRule;
import com.example.demo.fault.FaultStatus;
import com.example.demo.fault.FaultType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.util.List;
import java.util.ArrayList;

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
            if ("shipping-service".equals(rule.getTargetService())) {
                rule.setStatus(FaultStatus.ACTIVE);
                faultRegistry.put(rule.getRuleId(), rule);
                log.info("Registered fault rule: {}", rule.getRuleId());
            }
        } catch (Exception e) {
            log.error("Error processing fault rule", e);
        }
    }

    @KafkaListener(topics = "replay-control")
    public void consumeReplayCommand(String message) {
        try {
            JsonNode incoming = objectMapper.readTree(message);
            if (!"REPLAY_START".equals(incoming.path("type").asText())) {
                return;
            }
            
            JsonNode context = incoming.path("context");
            String targetEventType = context.path("eventType").asText();
            
            // Does this service process this event type? (Normally we'd check if we consume the topic)
            // If the forkEvent was PAYMENT_SUCCESS, it means payment-service just generated it.
            // The NEXT service (inventory-service) should consume it.
            // Let's just simulate the consumption of this event by routing it to the normal consume logic.
            // We just format it like a standard Kafka event that we normally listen to.
            
            String incomingTopicToSimulate = "inventory-events";
            
            boolean shouldHandle = false;
            if (incomingTopicToSimulate.equals("order-commands") && targetEventType.equals("START_SIMULATION")) shouldHandle = true;
            if (incomingTopicToSimulate.equals("order-events") && targetEventType.equals("PAYMENT_SUCCESS")) shouldHandle = true;
            if (incomingTopicToSimulate.equals("order-events") && targetEventType.equals("ORDER_CREATED")) shouldHandle = true;
            if (incomingTopicToSimulate.equals("order-events") && targetEventType.equals("SHIPPING_CREATED")) shouldHandle = true;
            if (incomingTopicToSimulate.equals("order-events") && targetEventType.contains("FAILED")) shouldHandle = true;
            
            if (incomingTopicToSimulate.equals("payment-events") && targetEventType.equals("PAYMENT_SUCCESS")) shouldHandle = true;
            if (incomingTopicToSimulate.equals("payment-events") && targetEventType.equals("PAYMENT_FAILED")) shouldHandle = true;
            
            if (incomingTopicToSimulate.equals("inventory-events") && targetEventType.equals("INVENTORY_RESERVED")) shouldHandle = true;
            
            if (shouldHandle) {
                log.info("Jumpstarting replay from state: {}", targetEventType);
                
                ObjectNode simulatedEvent = objectMapper.createObjectNode();
                simulatedEvent.put("eventId", context.path("causationId").asText());
                simulatedEvent.put("timelineId", incoming.path("timelineId").asText());
                simulatedEvent.put("eventType", targetEventType);
                simulatedEvent.put("aggregateId", context.path("aggregateId").asText());
                simulatedEvent.put("executionMode", incoming.path("executionMode").asText());
                
                ObjectNode simulatedPayload = (ObjectNode) context.path("payload");
                simulatedPayload.set("removedFaultIds", context.path("removedFaultIds"));
                simulatedPayload.put("seed", context.path("seed").asLong());
                
                simulatedEvent.set("payload", simulatedPayload);
                
                this.consume(objectMapper.writeValueAsString(simulatedEvent));
            }
            
        } catch (Exception e) {
            log.error("Error processing replay start", e);
        }
    }

    @KafkaListener(topics = "inventory-events")
    public void consume(String message) {
        try {
            JsonNode incoming = objectMapper.readTree(message);
            log.info("Received message: {}", incoming);
            
            String executionMode = incoming.path("executionMode").asText("NORMAL");
            List<String> removedFaultIds = new ArrayList<>();
            JsonNode payload = incoming.path("payload");
            if (payload.has("removedFaultIds") && payload.get("removedFaultIds").isArray()) {
                for (JsonNode idNode : payload.get("removedFaultIds")) {
                    removedFaultIds.add(idNode.asText());
                }
            }
            long experimentSeed = payload.path("seed").asLong(0);

            String failAt = payload.path("failAt").asText("none");
            String timelineId = incoming.path("timelineId").asText();
            String orderId = incoming.path("aggregateId").asText();
            String causationId = incoming.path("eventId").asText();
            
            String myEventType = "SHIPPING_CREATED";
            if ("shipping-service".equals("inventory-service") && "inventory-timeout".equals(failAt)) {
                myEventType = "INVENTORY_TIMEOUT";
            }
            if ("shipping-service".equals("payment-service") && "payment-failed".equals(failAt)) {
                myEventType = "PAYMENT_FAILED";
            }
            
            String incomingEvent = incoming.path("eventType").asText();
            if (incomingEvent.contains("FAILED") || incomingEvent.contains("TIMEOUT")) {
                if ("shipping-service".equals("order-service")) {
                    myEventType = "ORDER_FAILED";
                } else {
                    return; 
                }
            }
            if (incomingEvent.equals("SHIPPING_CREATED") && "shipping-service".equals("order-service")) {
                myEventType = "ORDER_COMPLETED";
            }
            
            // --- FAULT INTERCEPTOR START ---
            FaultRule activeFault = null;
            for (FaultRule rule : faultRegistry.values()) {
                if (rule.getStatus() == FaultStatus.ACTIVE && rule.getTargetEventType().equals(myEventType)) {
                    
                    // PHASE 8: Bypass if this fault is removed for this counterfactual execution
                    if ("COUNTERFACTUAL".equals(executionMode) && removedFaultIds.contains(rule.getRuleId())) {
                        log.info("Bypassing fault {} because it is removed in this experiment", rule.getRuleId());
                        continue;
                    }
                    
                    long seedToUse = "COUNTERFACTUAL".equals(executionMode) ? experimentSeed : rule.getSeed();
                    Random rand = new Random(seedToUse);
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
                emitEvent(timelineId, orderId, causationId, "FAULT_TRIGGERED", failAt, activeFault.getRuleId(), executionMode, removedFaultIds, experimentSeed);

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
                        emitEvent(timelineId, orderId, causationId, myEventType, failAt, null, executionMode, removedFaultIds, experimentSeed);
                        break;
                }
            }
            // --- FAULT INTERCEPTOR END ---

            if ("shipping-service".equals("payment-service") && myEventType.equals("PAYMENT_SUCCESS")) {
                 emitEvent(timelineId, orderId, causationId, "PAYMENT_STARTED", failAt, null, executionMode, removedFaultIds, experimentSeed);
                 Thread.sleep(200);
            }
            if ("shipping-service".equals("inventory-service")) {
                 emitEvent(timelineId, orderId, causationId, "INVENTORY_RESERVATION_REQUESTED", failAt, null, executionMode, removedFaultIds, experimentSeed);
                 Thread.sleep(200);
            }
            
            Thread.sleep(500);
            emitEvent(timelineId, orderId, causationId, myEventType, failAt, null, executionMode, removedFaultIds, experimentSeed);
            
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
    
    private void emitEvent(String timelineId, String aggregateId, String causationId, String eventType, String failAt, String faultRuleId, String executionMode, List<String> removedFaultIds, long seed) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("eventId", UUID.randomUUID().toString());
            root.put("timelineId", timelineId);
            root.put("serviceName", "shipping-service");
            root.put("eventType", eventType);
            root.put("aggregateType", faultRuleId != null ? "FAULT" : "ORDER");
            root.put("aggregateId", faultRuleId != null ? faultRuleId : aggregateId);
            root.put("causationId", causationId);
            root.put("correlationId", aggregateId);
            root.put("executionMode", executionMode);
            root.put("timestamp", Instant.now().toString());
            
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("failAt", failAt);
            if (faultRuleId != null) payload.put("ruleId", faultRuleId);
            
            if ("COUNTERFACTUAL".equals(executionMode)) {
                payload.put("seed", seed);
                ArrayNode faultsNode = objectMapper.createArrayNode();
                removedFaultIds.forEach(faultsNode::add);
                payload.set("removedFaultIds", faultsNode);
            }
            
            root.set("payload", payload);
            
            String outboundMessage = objectMapper.writeValueAsString(root);
            kafkaTemplate.send("shipping-events", outboundMessage);
            log.info("Emitted event: {}", eventType);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
