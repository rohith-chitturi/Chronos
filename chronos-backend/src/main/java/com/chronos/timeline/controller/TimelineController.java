package com.chronos.timeline.controller;

import com.chronos.timeline.dto.CounterfactualForkRequest;
import com.chronos.timeline.dto.CounterfactualForkRequest;
import com.chronos.timeline.dto.TimelineCreateDto;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.service.CounterfactualEngineService;
import com.chronos.timeline.service.TimelineComparisonService;
import com.chronos.timeline.service.TimelineForkingService;
import com.chronos.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/timelines")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;
    private final TimelineForkingService forkingService;
    private final CounterfactualEngineService counterfactualEngineService;
    private final TimelineComparisonService comparisonService;

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareTimelines(
            @RequestParam UUID base,
            @RequestParam UUID counterfactual) {
        return ResponseEntity.ok(comparisonService.compareTimelines(base, counterfactual));
    }

    @PostMapping
    public ResponseEntity<TimelineDto> createTimeline(@Validated @RequestBody TimelineCreateDto dto) {
        return new ResponseEntity<>(timelineService.createTimeline(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TimelineDto>> getAllTimelines() {
        return ResponseEntity.ok(timelineService.getAllTimelines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimelineDto> getTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(timelineService.getTimeline(id));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<TimelineDto>> getChildTimelines(@PathVariable UUID id) {
        return ResponseEntity.ok(timelineService.getChildTimelines(id));
    }

    @PostMapping("/{parentTimelineId}/fork/{forkEventId}")
    public ResponseEntity<TimelineDto> forkTimeline(
            @PathVariable UUID parentTimelineId,
            @PathVariable UUID forkEventId,
            @RequestParam String name) {
        return new ResponseEntity<>(forkingService.forkTimeline(parentTimelineId, forkEventId, name), HttpStatus.CREATED);
    }
    
    @PostMapping("/{timelineId}/fork")
    public ResponseEntity<TimelineDto> forkAndReplay(
            @PathVariable UUID timelineId,
            @RequestBody CounterfactualForkRequest request) {
        TimelineDto created = counterfactualEngineService.forkAndReplay(timelineId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
