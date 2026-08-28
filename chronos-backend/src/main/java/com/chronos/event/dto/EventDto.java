package com.chronos.event.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class EventDto {
    private UUID id;
    private UUID timelineId;
    private Long sequenceNumber;
    private Instant timestamp;
    private String serviceName;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private Object payload;
    private UUID causationId;
    private UUID correlationId;
    private UUID parentEventId;
    private Instant createdAt;
}
