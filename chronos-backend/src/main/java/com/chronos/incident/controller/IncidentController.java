package com.chronos.incident.controller;

import com.chronos.incident.dto.IncidentReport;
import com.chronos.incident.service.IncidentAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentAnalyzerService incidentAnalyzerService;
    private final com.chronos.investigation.service.AiInvestigatorService aiInvestigatorService;
    private final com.chronos.investigation.validation.InvestigationReportValidator validator;

    @GetMapping("/analyze")
    public ResponseEntity<IncidentReport> analyzeIncident(@RequestParam UUID eventId) {
        return ResponseEntity.ok(incidentAnalyzerService.analyzeIncident(eventId));
    }

    @org.springframework.web.bind.annotation.PostMapping("/investigate")
    public ResponseEntity<com.chronos.investigation.dto.InvestigationReport> investigateIncident(@RequestParam UUID eventId) {
        IncidentReport report = incidentAnalyzerService.analyzeIncident(eventId);
        com.chronos.investigation.dto.InvestigationReport investigation = aiInvestigatorService.investigate(report);
        validator.validate(investigation, report);
        return ResponseEntity.ok(investigation);
    }
}
