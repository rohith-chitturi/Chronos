package com.chronos;

import com.chronos.causality.repository.EventNodeRepository;
import com.chronos.causality.service.GraphRebuildService;
import com.chronos.event.entity.SystemEvent;
import com.chronos.event.repository.SystemEventRepository;
import com.chronos.timeline.dto.CounterfactualForkRequest;
import com.chronos.timeline.dto.TimelineDto;
import com.chronos.timeline.entity.Timeline;
import com.chronos.timeline.repository.TimelineRepository;
import com.chronos.timeline.service.CounterfactualEngineService;
import com.chronos.timeline.service.TimelineLineageResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DeterminismAndResilienceTest {

    @Autowired
    private CounterfactualEngineService engineService;

    @Autowired
    private TimelineLineageResolver lineageResolver;

    @Autowired
    private TimelineRepository timelineRepository;

    @Autowired
    private SystemEventRepository eventRepository;

    @Autowired
    private GraphRebuildService graphRebuildService;

    @Autowired
    private EventNodeRepository eventNodeRepository;

    @Test
    @Transactional
    public void testDeterminism_IdenticalForksYieldIdenticalLogicalExecution() {
        // Setup: Create MAIN timeline and some events
        Timeline main = timelineRepository.save(Timeline.builder()
                .name("MAIN")
                .status(com.chronos.timeline.entity.TimelineStatus.ACTIVE)
                .build());

        SystemEvent e1 = eventRepository.save(createEvent(main.getId(), 1L, "ORDER_CREATED"));
        SystemEvent e2 = eventRepository.save(createEvent(main.getId(), 2L, "PAYMENT_SUCCESS"));
        SystemEvent forkEvent = eventRepository.save(createEvent(main.getId(), 3L, "INVENTORY_REQUESTED"));
        SystemEvent e4 = eventRepository.save(createEvent(main.getId(), 4L, "INVENTORY_TIMEOUT"));

        // Fork EXP-002
        CounterfactualForkRequest req1 = new CounterfactualForkRequest();
        req1.setNewTimelineName("EXP-002");
        req1.setForkEventId(forkEvent.getId());
        req1.setSeed(42819L);
        req1.setRemovedFaultIds(List.of("FAULT-001"));
        TimelineDto exp2Dto = engineService.forkAndReplay(main.getId(), req1);

        // Fork EXP-003 exactly the same way
        CounterfactualForkRequest req2 = new CounterfactualForkRequest();
        req2.setNewTimelineName("EXP-003");
        req2.setForkEventId(forkEvent.getId());
        req2.setSeed(42819L);
        req2.setRemovedFaultIds(List.of("FAULT-001"));
        TimelineDto exp3Dto = engineService.forkAndReplay(main.getId(), req2);

        // Simulate identical replay execution for both
        eventRepository.save(createEvent(exp2Dto.getId(), 4L, "INVENTORY_RESERVED"));
        eventRepository.save(createEvent(exp3Dto.getId(), 4L, "INVENTORY_RESERVED"));
        
        eventRepository.save(createEvent(exp2Dto.getId(), 5L, "SHIPPING_CREATED"));
        eventRepository.save(createEvent(exp3Dto.getId(), 5L, "SHIPPING_CREATED"));

        // Resolve Lineage
        Timeline exp2 = timelineRepository.findById(exp2Dto.getId()).get();
        Timeline exp3 = timelineRepository.findById(exp3Dto.getId()).get();

        List<SystemEvent> history2 = lineageResolver.resolveHistory(exp2, null);
        List<SystemEvent> history3 = lineageResolver.resolveHistory(exp3, null);

        assertEquals(history2.size(), history3.size());
        
        for (int i = 0; i < history2.size(); i++) {
            assertEquals(history2.get(i).getEventType(), history3.get(i).getEventType());
            assertEquals(history2.get(i).getSequenceNumber(), history3.get(i).getSequenceNumber());
        }
    }

    @Test
    @Transactional
    public void testNestedLineageResolution() {
        Timeline main = timelineRepository.save(Timeline.builder().name("MAIN").status(com.chronos.timeline.entity.TimelineStatus.ACTIVE).build());
        SystemEvent e1 = eventRepository.save(createEvent(main.getId(), 1L, "E1"));
        SystemEvent e2 = eventRepository.save(createEvent(main.getId(), 2L, "E2"));

        Timeline exp1 = timelineRepository.save(Timeline.builder().name("EXP-1").parentTimelineId(main.getId()).forkEventId(e2.getId()).status(com.chronos.timeline.entity.TimelineStatus.ACTIVE).build());
        SystemEvent e3 = eventRepository.save(createEvent(exp1.getId(), 3L, "E3"));
        SystemEvent e4 = eventRepository.save(createEvent(exp1.getId(), 4L, "E4"));

        Timeline exp2 = timelineRepository.save(Timeline.builder().name("EXP-2").parentTimelineId(exp1.getId()).forkEventId(e4.getId()).status(com.chronos.timeline.entity.TimelineStatus.ACTIVE).build());
        SystemEvent e5 = eventRepository.save(createEvent(exp2.getId(), 5L, "E5"));

        List<SystemEvent> history = lineageResolver.resolveHistory(exp2, null);
        assertEquals(5, history.size());
        assertEquals("E1", history.get(0).getEventType());
        assertEquals("E3", history.get(2).getEventType());
        assertEquals("E5", history.get(4).getEventType());
    }

    @Test
    @Transactional
    public void testGraphRebuildFromPostgres() {
        Timeline main = timelineRepository.save(Timeline.builder().name("MAIN").status(com.chronos.timeline.entity.TimelineStatus.ACTIVE).build());
        eventRepository.save(createEvent(main.getId(), 1L, "E1"));
        eventRepository.save(createEvent(main.getId(), 2L, "E2"));

        eventNodeRepository.deleteAll();
        assertEquals(0, eventNodeRepository.count());

        graphRebuildService.rebuildGraph();
        assertTrue(eventNodeRepository.count() > 0);
    }

    private SystemEvent createEvent(UUID timelineId, Long seq, String type) {
        SystemEvent event = new SystemEvent();
        event.setTimelineId(timelineId);
        event.setSequenceNumber(seq);
        event.setEventType(type);
        event.setTimestamp(Instant.now());
        event.setAggregateId("ORDER-1");
        event.setAggregateType("ORDER");
        event.setPayload(new ObjectMapper().createObjectNode());
        return event;
    }
}
