package com.chronos.timeline.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CounterfactualForkRequest {
    private UUID forkEventId;
    private String newTimelineName;
    private List<String> removedFaultIds;
    private Long seed;
}
