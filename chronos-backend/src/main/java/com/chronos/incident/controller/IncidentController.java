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

    @GetMapping("/analyze")
    public ResponseEntity<IncidentReport> analyzeIncident(@RequestParam UUID eventId) {
        return ResponseEntity.ok(incidentAnalyzerService.analyzeIncident(eventId));
    }
}
