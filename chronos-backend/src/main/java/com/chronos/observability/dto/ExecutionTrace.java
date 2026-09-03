package com.chronos.observability.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ExecutionTrace {
    private UUID timelineId;
    private String correlationId;
    private Instant startTime;
    private Instant endTime;
    private long totalDurationMs;
    private List<TraceSegment> segments;
}
