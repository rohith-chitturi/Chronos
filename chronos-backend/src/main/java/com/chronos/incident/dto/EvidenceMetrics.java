package com.chronos.incident.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceMetrics {
    private int totalEvents;
    private int totalFaults;
    private int causalEdges;
}
