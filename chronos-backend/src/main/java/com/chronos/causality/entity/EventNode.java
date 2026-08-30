package com.chronos.causality.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node("Event")
@Data
public class EventNode {
    @Id
    private UUID eventId;
    private UUID timelineId;
    private String eventType;
    private String serviceName;
    private Instant timestamp;
    private UUID correlationId;

    @Relationship(type = "CAUSED", direction = Relationship.Direction.OUTGOING)
    private Set<EventNode> causedEvents = new HashSet<>();

    public void causes(EventNode event) {
        causedEvents.add(event);
    }
}
