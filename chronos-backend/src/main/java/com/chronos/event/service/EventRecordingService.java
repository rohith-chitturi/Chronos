package com.chronos.event.service;

import com.chronos.event.dto.EventDto;
import com.chronos.event.dto.EventRecordDto;
import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRecordingService {

    private final SystemEventRepository eventRepository;
    private final TimelineRepository timelineRepository;

    @Transactional
    public EventDto recordEvent(EventRecordDto dto) {
        // Validate timeline exists
        timelineRepository.findById(dto.getTimelineId())
                .orElseThrow(() -> new RuntimeException("Timeline not found"));

        // Determine next sequence number
        SystemEvent lastEvent = eventRepository.findFirstByTimelineIdOrderBySequenceNumberDesc(dto.getTimelineId());
        long nextSequence = (lastEvent != null) ? lastEvent.getSequenceNumber() + 1 : 1;

        // Immutable event creation
        SystemEvent event = SystemEvent.builder()
                .timelineId(dto.getTimelineId())
                .sequenceNumber(nextSequence)
                .timestamp(dto.getTimestamp())
                .serviceName(dto.getServiceName())
                .eventType(dto.getEventType())
                .aggregateType(dto.getAggregateType())
                .aggregateId(dto.getAggregateId())
                .payload(dto.getPayload())
                .causationId(dto.getCausationId())
                .correlationId(dto.getCorrelationId())
                .parentEventId(dto.getParentEventId())
                .build();

        event = eventRepository.save(event);
        return mapToDto(event);
    }

    public List<EventDto> getEventsByTimeline(UUID timelineId) {
        return eventRepository.findByTimelineIdOrderBySequenceNumberAsc(timelineId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EventDto mapToDto(SystemEvent event) {
        return EventDto.builder()
                .id(event.getId())
                .timelineId(event.getTimelineId())
                .sequenceNumber(event.getSequenceNumber())
                .timestamp(event.getTimestamp())
                .serviceName(event.getServiceName())
                .eventType(event.getEventType())
                .aggregateType(event.getAggregateType())
                .aggregateId(event.getAggregateId())
                .payload(event.getPayload())
                .causationId(event.getCausationId())
                .correlationId(event.getCorrelationId())
                .parentEventId(event.getParentEventId())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
