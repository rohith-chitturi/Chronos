package com.chronos.causality.service;

import com.chronos.causality.repository.EventNodeRepository;
import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphRebuildService {

    private final EventNodeRepository eventNodeRepository;
    private final SystemEventRepository systemEventRepository;
    private final CausalityProcessor causalityProcessor;

    @Transactional
    public void rebuildGraph() {
        log.info("Starting complete Neo4j graph rebuild from PostgreSQL");
        
        // 1. Clear existing graph
        eventNodeRepository.deleteAll();
        
        // 2. Fetch all events from PostgreSQL
        List<SystemEvent> allEvents = systemEventRepository.findAll();
        
        // 3. Process each event sequentially
        // Sort by timestamp and sequence to ensure causal ordering is preserved
        allEvents.sort((e1, e2) -> {
            int timeCompare = e1.getTimestamp().compareTo(e2.getTimestamp());
            if (timeCompare != 0) return timeCompare;
            return e1.getSequenceNumber().compareTo(e2.getSequenceNumber());
        });

        int count = 0;
        for (SystemEvent event : allEvents) {
            try {
                causalityProcessor.processEvent(event);
                count++;
            } catch (Exception e) {
                log.error("Failed to process event {} during rebuild", event.getId(), e);
            }
        }
        
        log.info("Successfully rebuilt graph with {} events", count);
    }
}
