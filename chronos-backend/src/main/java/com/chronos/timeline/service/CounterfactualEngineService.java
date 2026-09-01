package com.chronos.timeline.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.dto.CounterfactualForkRequest;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.entity.TimelineStatus;
import com.chronos.timeline.repository.TimelineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounterfactualEngineService {

    private final TimelineRepository timelineRepository;
    private final SystemEventRepository eventRepository;
    private final TimelineService timelineService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public TimelineDto forkAndReplay(UUID parentTimelineId, CounterfactualForkRequest request) {
        Timeline parent = timelineRepository.findById(parentTimelineId)
                .orElseThrow(() -> new RuntimeException("Parent timeline not found"));

        SystemEvent forkEvent = eventRepository.findById(request.getForkEventId())
                .orElseThrow(() -> new RuntimeException("Fork event not found"));

        Timeline counterfactualTimeline = Timeline.builder()
                .name(request.getNewTimelineName())
                .description("Counterfactual fork from " + parent.getName())
                .parentTimelineId(parentTimelineId)
                .forkEventId(request.getForkEventId())
                .status(TimelineStatus.ACTIVE)
                .executionMode("COUNTERFACTUAL")
                .seed(request.getSeed())
                .removedFaultIds(request.getRemovedFaultIds())
                .build();

        counterfactualTimeline = timelineRepository.save(counterfactualTimeline);

        // State Reconstruction: The fork event contains the payload that triggers the next state.
        // We package this into a REPLAY_START command.
        try {
            ObjectNode replayCommand = objectMapper.createObjectNode();
            replayCommand.put("type", "REPLAY_START");
            replayCommand.put("timelineId", counterfactualTimeline.getId().toString());
            replayCommand.put("experimentId", counterfactualTimeline.getId().toString());
            replayCommand.put("executionMode", "COUNTERFACTUAL");
            replayCommand.put("forkEventId", forkEvent.getId().toString());
            
            // Reconstruct the event context
            ObjectNode context = objectMapper.createObjectNode();
            context.put("eventType", forkEvent.getEventType());
            context.put("aggregateId", forkEvent.getAggregateId());
            context.put("causationId", forkEvent.getId().toString()); // new causal root
            context.set("payload", objectMapper.valueToTree(forkEvent.getPayload()));
            context.set("removedFaultIds", objectMapper.valueToTree(request.getRemovedFaultIds()));
            context.put("seed", request.getSeed());
            
            replayCommand.set("context", context);
            
            String commandJson = objectMapper.writeValueAsString(replayCommand);
            kafkaTemplate.send("replay-control", commandJson);
            
            log.info("Started counterfactual replay for timeline {} from event {}", counterfactualTimeline.getId(), forkEvent.getEventType());
        } catch (Exception e) {
            log.error("Failed to start replay", e);
        }

        return timelineService.mapToDto(counterfactualTimeline);
    }
}
