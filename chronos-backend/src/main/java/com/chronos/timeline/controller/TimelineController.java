package com.chronos.timeline.controller;

import com.chronos.timeline.dto.TimelineCreateDto;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.service.TimelineForkingService;
import com.chronos.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timelines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimelineController {

    private final TimelineService timelineService;
    private final TimelineForkingService forkingService;

    @PostMapping
    public ResponseEntity<TimelineDto> createTimeline(@Validated @RequestBody TimelineCreateDto dto) {
        return ResponseEntity.ok(timelineService.createTimeline(dto));
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

    @PostMapping("/{id}/fork")
    public ResponseEntity<TimelineDto> forkTimeline(
            @PathVariable UUID id,
            @RequestParam UUID forkEventId,
            @RequestParam String newTimelineName) {
        return ResponseEntity.ok(forkingService.forkTimeline(id, forkEventId, newTimelineName));
    }
}
