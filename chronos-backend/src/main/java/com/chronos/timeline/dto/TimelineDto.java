package com.chronos.timeline.dto;

import com.chronos.timeline.entity.TimelineStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TimelineDto {
    private UUID id;
    private String name;
    private String description;
    private UUID parentTimelineId;
    private UUID forkEventId;
    private Instant createdAt;
    private TimelineStatus status;
}
