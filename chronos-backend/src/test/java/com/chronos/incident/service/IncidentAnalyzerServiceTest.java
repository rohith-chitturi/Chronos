package com.chronos.incident.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.incident.dto.IncidentReport;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncidentAnalyzerServiceTest {

    @Mock
    private SystemEventRepository eventRepository;

    @Mock
    private TimelineRepository timelineRepository;

    @InjectMocks
    private IncidentAnalyzerService incidentAnalyzerService;

    private UUID failedEventId;
    private UUID timelineId;
    private SystemEvent failedEvent;

    @BeforeEach
    void setUp() {
        failedEventId = UUID.randomUUID();
        timelineId = UUID.randomUUID();
        
        failedEvent = SystemEvent.builder()
                .id(failedEventId)
                .timelineId(timelineId)
                .eventType("ORDER_FAILED")
                .serviceName("order-service")
                .timestamp(Instant.now())
                .sequenceNumber(5L)
                .causationId(null)
                .build();
    }

    @Test
    void testAnalyzeIncident_NoCounterfactual() {
        when(eventRepository.findById(failedEventId)).thenReturn(Optional.of(failedEvent));
        when(timelineRepository.findByParentTimelineId(timelineId)).thenReturn(List.of());

        IncidentReport report = incidentAnalyzerService.analyzeIncident(failedEventId);

        assertNotNull(report);
        assertEquals("ORDER_FAILED", report.getOutcome());
        assertEquals("NOT_AVAILABLE", report.getCounterfactualProof().getStatus());
        assertEquals(1, report.getEvidenceChain().size()); // Only the failed event itself
    }

    @Test
    void testAnalyzeIncident_AmbiguousExperiments() {
        when(eventRepository.findById(failedEventId)).thenReturn(Optional.of(failedEvent));
        
        Timeline exp1 = Timeline.builder().id(UUID.randomUUID()).name("EXP-001").executionMode("COUNTERFACTUAL").build();
        Timeline exp2 = Timeline.builder().id(UUID.randomUUID()).name("EXP-002").executionMode("COUNTERFACTUAL").build();
        
        when(timelineRepository.findByParentTimelineId(timelineId)).thenReturn(List.of(exp1, exp2));

        IncidentReport report = incidentAnalyzerService.analyzeIncident(failedEventId);

        assertNotNull(report);
        assertEquals("ORDER_FAILED", report.getOutcome());
        assertEquals("AMBIGUOUS", report.getCounterfactualProof().getStatus());
    }
}
