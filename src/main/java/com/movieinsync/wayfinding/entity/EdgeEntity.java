package com.movieinsync.wayfinding.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "edges")
public class EdgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromNodeId;

    private String toNodeId;

    private double distanceM;

    private boolean wheelchairAccessible;

    private double congestionMultiplier;

    public EdgeEntity() {
    }

    public EdgeEntity(
            String fromNodeId,
            String toNodeId,
            double distanceM,
            boolean wheelchairAccessible,
            double congestionMultiplier) {

        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.distanceM = distanceM;
        this.wheelchairAccessible = wheelchairAccessible;
        this.congestionMultiplier = congestionMultiplier;
    }

    public Long getId() {
        return id;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public double getDistanceM() {
        return distanceM;
    }

    public void setDistanceM(double distanceM) {
        this.distanceM = distanceM;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public double getCongestionMultiplier() {
        return congestionMultiplier;
    }

    public void setCongestionMultiplier(double congestionMultiplier) {
        this.congestionMultiplier = congestionMultiplier;
    }
}