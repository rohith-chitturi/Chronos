package com.chronos.timeline.repository;

import com.chronos.timeline.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, UUID> {
    List<Timeline> findByParentTimelineId(UUID parentTimelineId);
}
