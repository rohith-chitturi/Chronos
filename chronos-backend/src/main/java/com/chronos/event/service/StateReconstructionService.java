package com.chronos.event.service;

import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StateReconstructionService {

    private final SystemEventRepository eventRepository;
    private final TimelineRepository timelineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TimelineLineageResolver lineageResolver;

    public Map<String, ObjectNode> reconstructState(UUID timelineId, Long upToSequence) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new RuntimeException("Timeline not found"));

        List<SystemEvent> logicalEventStream = lineageResolver.resolveHistory(timeline, upToSequence);
        
        return replayEvents(logicalEventStream);
    }

    private Map<String, ObjectNode> replayEvents(List<SystemEvent> events) {
        // State grouped by aggregateType -> aggregateId -> State
        Map<String, ObjectNode> state = new HashMap<>();

        for (SystemEvent event : events) {
            if (event.getAggregateType() == null || event.getAggregateId() == null || event.getPayload() == null) {
                continue;
            }
            
            String key = event.getAggregateType() + ":" + event.getAggregateId();
            ObjectNode aggregateState = state.getOrDefault(key, objectMapper.createObjectNode());
            
            // Merge event payload into aggregate state
            JsonNode payloadNode = objectMapper.valueToTree(event.getPayload());
            if (payloadNode.isObject()) {
                merge(aggregateState, (ObjectNode) payloadNode);
            }
            
            state.put(key, aggregateState);
        }

        return state;
    }

    private void merge(ObjectNode mainNode, ObjectNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode value = updateNode.get(fieldName);
            if (value.isObject() && mainNode.has(fieldName) && mainNode.get(fieldName).isObject()) {
                merge((ObjectNode) mainNode.get(fieldName), (ObjectNode) value);
            } else {
                mainNode.set(fieldName, value);
            }
        }
    }
}
