package com.chronos.observability.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TraceSegment {
    private String service;
    private UUID startEventId;
    private String startEventType;
    private UUID endEventId;
    private String endEventType;
    private Instant startTime;
    private Instant endTime;
    private long durationMs;
    private SegmentType segmentType;
    private String causationType;
    private String faultId;
    private boolean isAnomaly;
}
