package com.movieinsync.wayfinding.repository;

import com.movieinsync.wayfinding.entity.ClosureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosureRepository extends JpaRepository<ClosureEntity, Long> {

    List<ClosureEntity> findByEdgeId(Long edgeId);
}