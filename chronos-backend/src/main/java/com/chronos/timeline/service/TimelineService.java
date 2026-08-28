package com.chronos.timeline.service;

import com.chronos.timeline.dto.TimelineCreateDto;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.entity.TimelineStatus;
import com.chronos.timeline.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineRepository timelineRepository;

    @Transactional
    public TimelineDto createTimeline(TimelineCreateDto createDto) {
        Timeline timeline = Timeline.builder()
                .name(createDto.getName())
                .description(createDto.getDescription())
                .status(TimelineStatus.ACTIVE)
                .build();
        
        timeline = timelineRepository.save(timeline);
        return mapToDto(timeline);
    }

    public List<TimelineDto> getAllTimelines() {
        return timelineRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TimelineDto getTimeline(UUID id) {
        Timeline timeline = timelineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timeline not found"));
        return mapToDto(timeline);
    }
    
    public List<TimelineDto> getChildTimelines(UUID parentId) {
        return timelineRepository.findByParentTimelineId(parentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TimelineDto mapToDto(Timeline timeline) {
        return TimelineDto.builder()
                .id(timeline.getId())
                .name(timeline.getName())
                .description(timeline.getDescription())
                .parentTimelineId(timeline.getParentTimelineId())
                .forkEventId(timeline.getForkEventId())
                .createdAt(timeline.getCreatedAt())
                .status(timeline.getStatus())
                .build();
    }
}
