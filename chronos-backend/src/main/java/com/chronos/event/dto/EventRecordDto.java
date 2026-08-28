package com.chronos.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class EventRecordDto {
    @NotNull(message = "Timeline ID is required")
    private UUID timelineId;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Event type is required")
    private String eventType;

    private String aggregateType;
    
    private String aggregateId;
    
    private Object payload;
    
    private UUID causationId;
    
    private UUID correlationId;
    
    private UUID parentEventId;
    
    private Instant timestamp = Instant.now();
}
