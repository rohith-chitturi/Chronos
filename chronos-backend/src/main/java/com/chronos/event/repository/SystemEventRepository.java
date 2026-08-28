package com.chronos.event.repository;

import com.chronos.event.entity.SystemEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemEventRepository extends JpaRepository<SystemEvent, UUID> {
    List<SystemEvent> findByTimelineIdOrderBySequenceNumberAsc(UUID timelineId);
    
    SystemEvent findFirstByTimelineIdOrderBySequenceNumberDesc(UUID timelineId);
    
    List<SystemEvent> findByTimelineIdAndSequenceNumberLessThanEqualOrderBySequenceNumberAsc(UUID timelineId, Long sequenceNumber);
}
