package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventHandlerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-commands")
    public void consume(String message) {
        try {
            JsonNode incoming = objectMapper.readTree(message);
            log.info("Received message: {}", incoming);
            
            String failAt = incoming.path("payload").path("failAt").asText("none");
            String timelineId = incoming.path("timelineId").asText();
            String orderId = incoming.path("aggregateId").asText();
            String causationId = incoming.path("eventId").asText();
            
            String myEventType = "ORDER_CREATED";
            if ("order-service".equals("inventory-service") && "inventory-timeout".equals(failAt)) {
                myEventType = "INVENTORY_TIMEOUT";
            }
            if ("order-service".equals("payment-service") && "payment-failed".equals(failAt)) {
                myEventType = "PAYMENT_FAILED";
            }
            
            String incomingEvent = incoming.path("eventType").asText();
            if (incomingEvent.contains("FAILED") || incomingEvent.contains("TIMEOUT")) {
                if ("order-service".equals("order-service")) {
                    myEventType = "ORDER_FAILED";
                } else {
                    return; 
                }
            }
            if (incomingEvent.equals("SHIPPING_CREATED") && "order-service".equals("order-service")) {
                myEventType = "ORDER_COMPLETED";
            }
            
            if ("order-service".equals("payment-service") && myEventType.equals("PAYMENT_SUCCESS")) {
                 emitEvent(timelineId, orderId, causationId, "PAYMENT_STARTED", failAt);
                 Thread.sleep(200);
            }
            if ("order-service".equals("inventory-service")) {
                 emitEvent(timelineId, orderId, causationId, "INVENTORY_RESERVATION_REQUESTED", failAt);
                 Thread.sleep(200);
            }
            
            Thread.sleep(500);
            emitEvent(timelineId, orderId, causationId, myEventType, failAt);
            
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }
    
    private void emitEvent(String timelineId, String aggregateId, String causationId, String eventType, String failAt) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("eventId", UUID.randomUUID().toString());
            root.put("timelineId", timelineId);
            root.put("serviceName", "order-service");
            root.put("eventType", eventType);
            root.put("aggregateType", "ORDER");
            root.put("aggregateId", aggregateId);
            root.put("causationId", causationId);
            root.put("correlationId", aggregateId);
            root.put("timestamp", Instant.now().toString());
            
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("failAt", failAt);
            root.set("payload", payload);
            
            String outboundMessage = objectMapper.writeValueAsString(root);
            kafkaTemplate.send("order-events", outboundMessage);
            log.info("Emitted event: {}", eventType);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
