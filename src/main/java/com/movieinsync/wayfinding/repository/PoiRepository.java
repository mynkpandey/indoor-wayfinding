package com.movieinsync.wayfinding.repository;

import com.movieinsync.wayfinding.entity.PoiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoiRepository extends JpaRepository<PoiEntity, Long> {

    List<PoiEntity> findByType(String type);

    List<PoiEntity> findByNodeId(String nodeId);
}