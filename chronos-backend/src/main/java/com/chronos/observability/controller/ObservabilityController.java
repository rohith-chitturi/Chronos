package com.chronos.observability.controller;

import com.chronos.observability.dto.ExecutionTrace;
import com.chronos.observability.service.ExecutionTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/observability")
@RequiredArgsConstructor
public class ObservabilityController {

    private final ExecutionTraceService executionTraceService;

    @GetMapping("/traces/{timelineId}")
    public ResponseEntity<ExecutionTrace> getExecutionTrace(@PathVariable UUID timelineId) {
        return ResponseEntity.ok(executionTraceService.generateTrace(timelineId));
    }
}
