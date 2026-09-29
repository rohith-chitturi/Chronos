package com.chronos.investigation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InvestigationReport {
    private String summary;
    private List<String> facts;
    private List<String> inferences;
    private List<String> unknowns;
    private EvidenceQuality evidenceQuality;
}
