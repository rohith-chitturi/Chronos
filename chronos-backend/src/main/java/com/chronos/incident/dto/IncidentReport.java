package com.chronos.incident.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class IncidentReport {
    private String incidentId;
    private UUID timelineId;
    private UUID failedEventId;
    private String outcome;
    
    private String rootCauseAttribution;
    private List<String> causalChain;
    
    private List<String> faultAnalysis; // e.g. "LATENCY-01 on payment-service"
    private String traceImpact; // e.g. "+3000 ms causal delay"
    
    private CounterfactualProof counterfactualProof;
    private List<EvidenceNode> evidenceChain;
    private EvidenceMetrics evidenceMetrics;
}
