package com.chronos.event.controller;

import com.chronos.event.dto.EventDto;
import com.chronos.event.dto.EventRecordDto;
import com.chronos.event.service.EventRecordingService;
import com.chronos.event.service.StateReconstructionService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventRecordingService eventRecordingService;
    private final StateReconstructionService stateReconstructionService;

    @PostMapping
    public ResponseEntity<EventDto> recordEvent(@Validated @RequestBody EventRecordDto dto) {
        return ResponseEntity.ok(eventRecordingService.recordEvent(dto));
    }

    @GetMapping("/timeline/{timelineId}")
    public ResponseEntity<List<EventDto>> getEventsByTimeline(@PathVariable UUID timelineId) {
        return ResponseEntity.ok(eventRecordingService.getEventsByTimeline(timelineId));
    }

    @GetMapping("/timeline/{timelineId}/state")
    public ResponseEntity<Map<String, ObjectNode>> getTimelineState(
            @PathVariable UUID timelineId,
            @RequestParam(required = false) Long upToSequence) {
        return ResponseEntity.ok(stateReconstructionService.reconstructState(timelineId, upToSequence));
    }
}
