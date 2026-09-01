package com.chronos.timeline.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.service.StateReconstructionService;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimelineComparisonService {

    private final TimelineRepository timelineRepository;
    private final TimelineLineageResolver lineageResolver;
    private final StateReconstructionService stateReconstructionService;

    public Map<String, Object> compareTimelines(UUID baseId, UUID counterfactualId) {
        Timeline base = timelineRepository.findById(baseId)
                .orElseThrow(() -> new RuntimeException("Base timeline not found"));
        Timeline counterfactual = timelineRepository.findById(counterfactualId)
                .orElseThrow(() -> new RuntimeException("Counterfactual timeline not found"));

        List<SystemEvent> baseEvents = lineageResolver.resolveHistory(base, null);
        List<SystemEvent> cfEvents = lineageResolver.resolveHistory(counterfactual, null);

        // Find divergence
        SystemEvent divergenceEvent = null;
        for (SystemEvent cfEvent : cfEvents) {
            // A generated event is one that specifically belongs to the counterfactual timeline ID
            if (cfEvent.getTimelineId().equals(counterfactualId)) {
                divergenceEvent = cfEvent;
                break;
            }
        }

        long inheritedCount = cfEvents.stream().filter(e -> !e.getTimelineId().equals(counterfactualId)).count();
        long generatedCount = cfEvents.stream().filter(e -> e.getTimelineId().equals(counterfactualId)).count();

        // Get Final States
        Map<String, ObjectNode> baseStateMap = stateReconstructionService.reconstructState(baseId, null);
        Map<String, ObjectNode> cfStateMap = stateReconstructionService.reconstructState(counterfactualId, null);

        // Compute State Diff (simplified for Order status)
        Map<String, Object> stateDiff = new HashMap<>();
        String baseOutcome = "UNKNOWN";
        String cfOutcome = "UNKNOWN";

        for (Map.Entry<String, ObjectNode> entry : baseStateMap.entrySet()) {
            if (entry.getKey().startsWith("ORDER:")) {
                if (entry.getValue().has("failAt") && entry.getValue().get("failAt").asText().contains("timeout")) {
                    baseOutcome = "FAILED";
                }
            }
        }
        for (Map.Entry<String, ObjectNode> entry : cfStateMap.entrySet()) {
            if (entry.getKey().startsWith("ORDER:")) {
                if (!entry.getValue().has("failAt") || entry.getValue().get("failAt").asText().equals("none")) {
                    cfOutcome = "COMPLETED"; // naive check for demo
                }
            }
        }
        
        // Actually look at the last event of each timeline for outcome
        if (!baseEvents.isEmpty()) {
            String lastType = baseEvents.get(baseEvents.size() - 1).getEventType();
            if (lastType.contains("FAILED")) baseOutcome = "FAILED";
            else if (lastType.contains("COMPLETED")) baseOutcome = "COMPLETED";
        }
        
        if (!cfEvents.isEmpty()) {
            String lastType = cfEvents.get(cfEvents.size() - 1).getEventType();
            if (lastType.contains("FAILED")) cfOutcome = "FAILED";
            else if (lastType.contains("COMPLETED")) cfOutcome = "COMPLETED";
        }

        Map<String, Object> orderStatusDiff = new HashMap<>();
        orderStatusDiff.put("before", baseOutcome);
        orderStatusDiff.put("after", cfOutcome);
        stateDiff.put("order.status", orderStatusDiff);

        Map<String, Object> response = new HashMap<>();
        response.put("baseOutcome", baseOutcome);
        response.put("counterfactualOutcome", cfOutcome);
        response.put("forkEvent", counterfactual.getForkEventId());
        response.put("divergenceEvent", divergenceEvent != null ? divergenceEvent.getId() : null);
        response.put("inheritedEventCount", inheritedCount);
        response.put("generatedEventCount", generatedCount);
        response.put("removedFaults", counterfactual.getRemovedFaultIds());
        
        Map<String, Object> replayInfo = new HashMap<>();
        replayInfo.put("deterministic", true);
        replayInfo.put("seed", counterfactual.getSeed());
        response.put("replay", replayInfo);
        
        response.put("stateDiff", stateDiff);

        return response;
    }
}
