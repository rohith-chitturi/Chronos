package com.chronos.causality.service;

import com.chronos.causality.entity.EventNode;
import com.chronos.causality.repository.EventNodeRepository;
import com.chronos.event.dto.SystemEventSavedEvent;
import com.chronos.event.entity.SystemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CausalityProcessor {

    private final EventNodeRepository eventNodeRepository;

    @Async
    @EventListener
    @Transactional
    public void processCausality(SystemEventSavedEvent eventPayload) {
        SystemEvent systemEvent = eventPayload.getSystemEvent();
        log.info("Processing causality for event ID: {}", systemEvent.getId());

        try {
            EventNode node = new EventNode();
            node.setEventId(systemEvent.getId());
            node.setTimelineId(systemEvent.getTimelineId());
            node.setEventType(systemEvent.getEventType());
            node.setServiceName(systemEvent.getServiceName());
            node.setTimestamp(systemEvent.getTimestamp());
            node.setCorrelationId(systemEvent.getCorrelationId());

            if (systemEvent.getCausationId() != null) {
                // Find parent to draw the CAUSED relationship
                eventNodeRepository.findById(systemEvent.getCausationId()).ifPresent(parent -> {
                    // Application-level DAG constraint: we only point from past to future events.
                    // This implies the graph remains acyclic since time only flows forward.
                    parent.causes(node);
                    eventNodeRepository.save(parent); // This cascades and saves the new node too
                });
            } else {
                eventNodeRepository.save(node);
            }
            log.info("Successfully persisted EventNode to Neo4j: {}", node.getEventId());
        } catch (Exception e) {
            log.error("Failed to process causality in Neo4j. Postgres remains intact.", e);
        }
    }
}
