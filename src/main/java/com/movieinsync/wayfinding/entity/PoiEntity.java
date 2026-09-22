package com.movieinsync.wayfinding.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pois")
public class PoiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nodeId;

    private String type;

    public PoiEntity() {
    }

    public PoiEntity(String nodeId, String type) {
        this.nodeId = nodeId;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}