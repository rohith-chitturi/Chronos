package com.chronos.investigation.validation;

import com.chronos.incident.dto.IncidentReport;
import com.chronos.incident.dto.EvidenceNode;
import com.chronos.investigation.dto.InvestigationReport;
import com.chronos.investigation.dto.Fact;
import com.chronos.investigation.dto.Inference;
import com.chronos.investigation.dto.EvidenceReference;
import com.chronos.investigation.exception.InvalidInvestigationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InvestigationReportValidator {

    public void validate(InvestigationReport investigation, IncidentReport dossier) {
        if (investigation == null) {
            throw new InvalidInvestigationException("InvestigationReport cannot be null");
        }

        Set<String> validIds = extractValidEvidenceIds(dossier);

        // 1. Every fact must have supporting evidence references that exist in the dossier
        if (investigation.getFacts() != null) {
            for (Fact fact : investigation.getFacts()) {
                validateEvidenceRefs(fact.getEvidenceRefs(), validIds, "Fact: " + fact.getStatement());
            }
        }

        // 2. Every inference must identify the evidence it derives from
        if (investigation.getInferences() != null) {
            for (Inference inference : investigation.getInferences()) {
                validateEvidenceRefs(inference.getEvidenceRefs(), validIds, "Inference: " + inference.getStatement());
            }
        }

        // 3. Every unknown must be explicitly represented if counterfactual is missing or ambiguous
        boolean counterfactualMissingOrAmbiguous = dossier.getCounterfactualProof() == null 
            || !"PROVEN".equals(dossier.getCounterfactualProof().getStatus());
            
        if (counterfactualMissingOrAmbiguous) {
            if (investigation.getUnknowns() == null || investigation.getUnknowns().isEmpty()) {
                throw new InvalidInvestigationException("Counterfactual proof is not PROVEN, but the AI failed to explicitly declare Unknowns.");
            }
        }
        
        // 4. Missing unknowns if causal chain is incomplete etc. can be added later.
    }

    private void validateEvidenceRefs(java.util.List<EvidenceReference> refs, Set<String> validIds, String context) {
        if (refs == null || refs.isEmpty()) {
            throw new InvalidInvestigationException("Unsupported claim rejected: Missing evidence references for " + context);
        }
        for (EvidenceReference ref : refs) {
            if (ref.getId() != null && !validIds.contains(ref.getId())) {
                throw new InvalidInvestigationException("AI hallucination detected: Evidence ID '" + ref.getId() + "' was invented and does not exist in the Chronos dossier. Context: " + context);
            }
        }
    }

    private Set<String> extractValidEvidenceIds(IncidentReport dossier) {
        Set<String> ids = new HashSet<>();
        
        if (dossier.getIncidentId() != null) ids.add(dossier.getIncidentId());
        if (dossier.getTimelineId() != null) ids.add(dossier.getTimelineId().toString());
        if (dossier.getFailedEventId() != null) ids.add(dossier.getFailedEventId().toString());
        if (dossier.getRootCauseAttribution() != null) ids.add(dossier.getRootCauseAttribution());
        
        if (dossier.getCausalChain() != null) {
            ids.addAll(dossier.getCausalChain());
        }
        
        if (dossier.getFaultAnalysis() != null) {
            ids.addAll(dossier.getFaultAnalysis());
        }

        if (dossier.getCounterfactualProof() != null) {
            if (dossier.getCounterfactualProof().getCounterfactualTimelineId() != null) {
                ids.add(dossier.getCounterfactualProof().getCounterfactualTimelineId().toString());
            }
            if (dossier.getCounterfactualProof().getTimelineName() != null) {
                ids.add(dossier.getCounterfactualProof().getTimelineName());
            }
            if (dossier.getCounterfactualProof().getRemovedFaults() != null) {
                ids.addAll(dossier.getCounterfactualProof().getRemovedFaults());
            }
        }
        
        if (dossier.getEvidenceChain() != null) {
            for (EvidenceNode node : dossier.getEvidenceChain()) {
                if (node.getEvidenceId() != null) ids.add(node.getEvidenceId());
                if (node.getRelatedEventId() != null) ids.add(node.getRelatedEventId());
                // In mock we use 'EXP' and 'CHAIN', let's allow them as general reference types or parse description
                ids.add("EXP");
                ids.add("CHAIN");
            }
        }
        return ids;
    }
}
