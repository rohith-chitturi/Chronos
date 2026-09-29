package com.chronos.investigation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fact {
    private String statement;
    private List<EvidenceReference> evidenceRefs;
}
