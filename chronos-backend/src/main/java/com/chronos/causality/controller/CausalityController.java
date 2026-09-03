package com.chronos.causality.controller;

import com.chronos.causality.entity.EventNode;
import com.chronos.causality.repository.EventNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CausalityController {

    private final EventNodeRepository eventNodeRepository;
    private final com.chronos.causality.service.GraphRebuildService graphRebuildService;

    @PostMapping("/rebuild")
    public ResponseEntity<Void> rebuildGraph() {
        graphRebuildService.rebuildGraph();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timelines/{timelineId}/causality")
    public ResponseEntity<List<EventNode>> getTimelineCausality(@PathVariable UUID timelineId) {
        return ResponseEntity.ok(eventNodeRepository.findByTimelineId(timelineId));
    }

    @GetMapping("/events/{eventId}/causes")
    public ResponseEntity<List<EventNode>> getEventCauses(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventNodeRepository.findCauses(eventId));
    }

    @GetMapping("/events/{eventId}/effects")
    public ResponseEntity<List<EventNode>> getEventEffects(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventNodeRepository.findEffects(eventId));
    }

    @GetMapping("/events/{eventId}/root-cause")
    public ResponseEntity<Map<String, Object>> getRootCause(@PathVariable UUID eventId) {
        EventNode target = eventNodeRepository.findById(eventId).orElseThrow();
        EventNode rootCause = eventNodeRepository.findRootCause(eventId);
        List<EventNode> chain = eventNodeRepository.findCauses(eventId);
        
        // Include the target event in the chain to match the exact user request format
        chain.add(target);
        
        List<String> chainNames = chain.stream()
            .map(EventNode::getEventType)
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "targetEvent", target.getEventType(),
            "rootCause", rootCause != null ? rootCause.getEventType() : target.getEventType(),
            "chain", chainNames
        ));
    }
}
