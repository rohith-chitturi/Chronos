package com.chronos.timeline.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.dto.TimelineCreateDto;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.entity.TimelineStatus;
import com.chronos.timeline.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimelineForkingService {

    private final TimelineRepository timelineRepository;
    private final SystemEventRepository eventRepository;
    private final TimelineService timelineService;

    @Transactional
    public TimelineDto forkTimeline(UUID parentTimelineId, UUID forkEventId, String newTimelineName) {
        // Validate parent exists
        Timeline parent = timelineRepository.findById(parentTimelineId)
                .orElseThrow(() -> new RuntimeException("Parent timeline not found"));

        // Validate fork event exists and belongs to parent
        SystemEvent forkEvent = eventRepository.findById(forkEventId)
                .orElseThrow(() -> new RuntimeException("Fork event not found"));

        if (!forkEvent.getTimelineId().equals(parentTimelineId)) {
            throw new RuntimeException("Fork event does not belong to the specified parent timeline");
        }

        // Create the child timeline (logical fork)
        Timeline childTimeline = Timeline.builder()
                .name(newTimelineName)
                .description("Forked from " + parent.getName() + " at event " + forkEvent.getSequenceNumber())
                .parentTimelineId(parentTimelineId)
                .forkEventId(forkEventId)
                .status(TimelineStatus.ACTIVE)
                .build();

        childTimeline = timelineRepository.save(childTimeline);
        return timelineService.mapToDto(childTimeline);
    }
}
