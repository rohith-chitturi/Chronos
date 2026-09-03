package com.chronos.observability.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.observability.dto.ExecutionTrace;
import com.chronos.observability.dto.SegmentType;
import com.chronos.observability.dto.TraceSegment;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import com.chronos.timeline.service.TimelineLineageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionTraceService {

    private final TimelineRepository timelineRepository;
    private final TimelineLineageResolver lineageResolver;

    public ExecutionTrace generateTrace(UUID timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new RuntimeException("Timeline not found"));

        List<SystemEvent> history = lineageResolver.resolveHistory(timeline, null);
        
        if (history.isEmpty()) {
            return ExecutionTrace.builder()
                    .timelineId(timelineId)
                    .segments(Collections.emptyList())
                    .build();
        }

        Map<UUID, SystemEvent> eventMap = history.stream()
                .collect(Collectors.toMap(SystemEvent::getId, Function.identity()));

        List<TraceSegment> segments = new ArrayList<>();
        
        Instant traceStart = history.get(0).getTimestamp();
        Instant traceEnd = history.get(history.size() - 1).getTimestamp();

        for (SystemEvent event : history) {
            if (event.getCausationId() != null && eventMap.containsKey(event.getCausationId())) {
                SystemEvent cause = eventMap.get(event.getCausationId());
                
                SegmentType type;
                String service;
                
                if (cause.getServiceName().equals(event.getServiceName())) {
                    type = SegmentType.PROCESSING;
                    service = event.getServiceName();
                } else {
                    type = SegmentType.INTER_SERVICE;
                    service = cause.getServiceName() + " -> " + event.getServiceName();
                }

                long duration = event.getTimestamp().toEpochMilli() - cause.getTimestamp().toEpochMilli();
                
                // Anomaly Detection Rule: 
                // If a latency fault was explicitly injected, the delay will be > 2000ms.
                // We'll deterministically flag this for demo purposes.
                boolean isAnomaly = false;
                String faultId = null;
                
                // If the causation event was FAULT_TRIGGERED, this is a fault delay
                if (cause.getEventType().equals("FAULT_TRIGGERED")) {
                    type = SegmentType.FAULT_DELAY;
                    isAnomaly = true;
                    // Extract ruleId from payload if possible, or just mark it
                    faultId = "LATENCY_FAULT"; // Simplified
                } else if (duration > 2000) {
                    isAnomaly = true;
                }

                segments.add(TraceSegment.builder()
                        .service(service)
                        .startEventId(cause.getId())
                        .startEventType(cause.getEventType())
                        .endEventId(event.getId())
                        .endEventType(event.getEventType())
                        .startTime(cause.getTimestamp())
                        .endTime(event.getTimestamp())
                        .durationMs(duration)
                        .segmentType(type)
                        .causationType(type.name())
                        .isAnomaly(isAnomaly)
                        .faultId(faultId)
                        .build());
            }
        }

        return ExecutionTrace.builder()
                .timelineId(timelineId)
                .correlationId(history.get(0).getAggregateId())
                .startTime(traceStart)
                .endTime(traceEnd)
                .totalDurationMs(traceEnd.toEpochMilli() - traceStart.toEpochMilli())
                .segments(segments)
                .build();
    }
}
