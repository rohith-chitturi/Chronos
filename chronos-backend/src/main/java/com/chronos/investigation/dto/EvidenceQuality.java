package com.chronos.investigation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceQuality {
    private String evidenceCompleteness;
    private String counterfactualValidation;
    private String directFaultAttribution;
    private String causalChain;
}
