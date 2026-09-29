package com.chronos.investigation.service;

import com.chronos.incident.dto.IncidentReport;
import com.chronos.investigation.dto.EvidenceQuality;
import com.chronos.investigation.dto.InvestigationReport;
import com.chronos.investigation.dto.Fact;
import com.chronos.investigation.dto.Inference;
import com.chronos.investigation.dto.EvidenceReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@ConditionalOnProperty(name = "chronos.ai.provider", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockAiInvestigatorService implements AiInvestigatorService {

    @Override
    public InvestigationReport investigate(IncidentReport incident) {
        log.info("Generating prompt for AI investigation based on Incident: {}", incident.getIncidentId());
        
        // This is the explicit contract: The AI only receives the deterministic evidence, not raw logs.
        String prompt = buildSystemPrompt(incident);
        log.debug("AI Prompt:\n{}", prompt);

        // Mocking the AI response based on the presence of counterfactual proof
        boolean hasProof = incident.getCounterfactualProof() != null 
                && !"AMBIGUOUS".equals(incident.getCounterfactualProof().getStatus());

        return InvestigationReport.builder()
                .summary(hasProof 
                    ? "The incident was deterministically caused by a simulated fault. Counterfactual evidence confirms that removing the fault allows the timeline to complete successfully." 
                    : "The incident caused a failure in the causal chain. No counterfactual evidence is available to definitively prove the root cause, but causal attribution points to a specific event.")
                .facts(Arrays.asList(
                    Fact.builder().statement("Event " + incident.getRootCauseAttribution() + " was identified as the root cause in the causal chain.")
                        .evidenceRefs(Arrays.asList(EvidenceReference.builder().type(com.chronos.investigation.dto.EvidenceType.EVENT).id(incident.getRootCauseAttribution()).build())).build(),
                    Fact.builder().statement("The causal chain contains " + (incident.getEvidenceMetrics() != null ? incident.getEvidenceMetrics().getTotalEvents() : 0) + " nodes.")
                        .evidenceRefs(Arrays.asList(EvidenceReference.builder().type(com.chronos.investigation.dto.EvidenceType.CAUSAL_EDGE).id("CHAIN").build())).build()
                ))
                .inferences(Arrays.asList(
                    Inference.builder().statement("The fault is causally associated with the transaction failure.")
                        .evidenceRefs(Arrays.asList(EvidenceReference.builder().type(com.chronos.investigation.dto.EvidenceType.FAULT).id(incident.getRootCauseAttribution()).build())).build(),
                    Inference.builder().statement(hasProof ? "If the fault had not occurred, the transaction would have succeeded." : "It is highly probable that the fault directly caused the drop in service availability.")
                        .evidenceRefs(hasProof ? Arrays.asList(EvidenceReference.builder().type(com.chronos.investigation.dto.EvidenceType.COUNTERFACTUAL).id("EXP").build()) : java.util.Collections.emptyList()).build()
                ))
                .unknowns(Arrays.asList(
                    hasProof ? "None. Counterfactual evidence provides complete certainty." : "Chronos could not establish what would have happened if the fault was removed, as no counterfactual experiment was run."
                ))
                .evidenceQuality(EvidenceQuality.builder()
                        .evidenceCompleteness(hasProof ? "HIGH" : "MEDIUM")
                        .counterfactualValidation(hasProof ? "AVAILABLE" : "UNAVAILABLE")
                        .directFaultAttribution(incident.getRootCauseAttribution() != null ? "YES" : "NO")
                        .causalChain("COMPLETE")
                        .build())
                .build();
    }

    private String buildSystemPrompt(IncidentReport incident) {
        return "You are an Evidence-Grounded AI Investigator.\n" +
               "Your task is to analyze the following deterministic evidence dossier produced by Chronos.\n" +
               "You must strictly distinguish between Facts (direct evidence), Inferences (logical conclusions), and Unknowns (missing evidence).\n" +
               "DO NOT hallucinate metrics, root causes, or architectural details not present in this report.\n\n" +
               "--- CHRONOS INCIDENT DOSSIER ---\n" +
               "Incident ID: " + incident.getIncidentId() + "\n" +
               "Root Cause Attribution: " + incident.getRootCauseAttribution() + "\n" +
               "Causal Chain: " + incident.getCausalChain() + "\n" +
               "Counterfactual Proof: " + incident.getCounterfactualProof() + "\n" +
               "Evidence Metrics: " + incident.getEvidenceMetrics();
    }
}
