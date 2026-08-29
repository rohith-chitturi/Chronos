package com.chronos.event.service;

import com.chronos.event.dto.EventRecordDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChronosEventCollector {

    private final EventRecordingService eventRecordingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"order-events", "payment-events", "inventory-events", "shipping-events"})
    public void consumeEvent(String message) {
        try {
            log.info("Received event via Kafka: {}", message);
            EventRecordDto dto = objectMapper.readValue(message, EventRecordDto.class);
            eventRecordingService.recordEvent(dto);
            log.info("Successfully recorded event: {} for timeline: {}", dto.getEventType(), dto.getTimelineId());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize Kafka message: {}", message, e);
        } catch (Exception e) {
            log.error("Failed to record event from Kafka: {}", e.getMessage(), e);
        }
    }
}
