package com.chronos.incident.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CounterfactualProof {
    private UUID counterfactualTimelineId;
    private String timelineName;
    private List<String> removedFaults;
    private String whatIfOutcome;
    private long realDurationMs;
    private long whatIfDurationMs;
    private String status; // e.g. "NOT_AVAILABLE", "AMBIGUOUS", "PROVEN"
}
