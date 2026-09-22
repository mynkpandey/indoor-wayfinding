package com.movieinsync.wayfinding.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "closures")
public class ClosureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long edgeId;

    private LocalTime closedFrom;

    private LocalTime closedUntil;

    public ClosureEntity() {
    }

    public ClosureEntity(
            Long edgeId,
            LocalTime closedFrom,
            LocalTime closedUntil) {

        this.edgeId = edgeId;
        this.closedFrom = closedFrom;
        this.closedUntil = closedUntil;
    }

    public Long getId() {
        return id;
    }

    public Long getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Long edgeId) {
        this.edgeId = edgeId;
    }

    public LocalTime getClosedFrom() {
        return closedFrom;
    }

    public void setClosedFrom(LocalTime closedFrom) {
        this.closedFrom = closedFrom;
    }

    public LocalTime getClosedUntil() {
        return closedUntil;
    }

    public void setClosedUntil(LocalTime closedUntil) {
        this.closedUntil = closedUntil;
    }
}