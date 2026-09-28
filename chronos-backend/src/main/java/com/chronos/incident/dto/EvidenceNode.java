package com.chronos.incident.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EvidenceNode {
    private String evidenceId;
    private String type; // EVENT, FAULT, COUNTERFACTUAL
    private String description;
    private Instant timestamp;
    private String relatedEventId;
}
