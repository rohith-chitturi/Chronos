package com.chronos.timeline.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineLineageResolver {

    private final TimelineRepository timelineRepository;
    private final SystemEventRepository eventRepository;

    public List<SystemEvent> resolveHistory(Timeline timeline, Long upToSequence) {
        List<SystemEvent> stream = new ArrayList<>();
        resolveRecursive(timeline, stream, upToSequence);
        // Sort by timestamp and sequence
        stream.sort(Comparator.comparing(SystemEvent::getTimestamp).thenComparing(SystemEvent::getSequenceNumber));
        return stream;
    }

    private void resolveRecursive(Timeline currentTimeline, List<SystemEvent> accumulator, Long upToSequence) {
        if (currentTimeline.getParentTimelineId() != null && currentTimeline.getForkEventId() != null) {
            Timeline parentTimeline = timelineRepository.findById(currentTimeline.getParentTimelineId())
                    .orElseThrow(() -> new RuntimeException("Parent timeline not found: " + currentTimeline.getParentTimelineId()));
            
            SystemEvent forkEvent = eventRepository.findById(currentTimeline.getForkEventId())
                    .orElseThrow(() -> new RuntimeException("Fork event not found: " + currentTimeline.getForkEventId()));

            // Recursively fetch parent lineage UP TO the fork event sequence
            resolveRecursive(parentTimeline, accumulator, forkEvent.getSequenceNumber());
        }

        // Fetch this timeline's events
        List<SystemEvent> currentEvents;
        if (upToSequence != null) {
            currentEvents = eventRepository.findByTimelineIdAndSequenceNumberLessThanEqualOrderBySequenceNumberAsc(
                    currentTimeline.getId(), upToSequence);
        } else {
            currentEvents = eventRepository.findByTimelineIdOrderBySequenceNumberAsc(currentTimeline.getId());
        }
        
        accumulator.addAll(currentEvents);
    }
}
