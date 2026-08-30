package com.chronos.causality.repository;

import com.chronos.causality.entity.EventNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventNodeRepository extends Neo4jRepository<EventNode, UUID> {
    
    // Cypher query to get upstream causes
    @Query("MATCH path = (e:Event {eventId: $eventId})<-[:CAUSED*]-(cause:Event) RETURN cause")
    List<EventNode> findCauses(UUID eventId);
    
    // Cypher query to get downstream effects
    @Query("MATCH path = (e:Event {eventId: $eventId})-[:CAUSED*]->(effect:Event) RETURN effect")
    List<EventNode> findEffects(UUID eventId);
    
    // Cypher query for the root cause
    @Query("MATCH path = (e:Event {eventId: $eventId})<-[:CAUSED*]-(root:Event) WHERE NOT ()-[:CAUSED]->(root) RETURN root LIMIT 1")
    EventNode findRootCause(UUID eventId);
    
    // Get all events for a timeline
    List<EventNode> findByTimelineId(UUID timelineId);
}
