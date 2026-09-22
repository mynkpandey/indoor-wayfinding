package com.movieinsync.wayfinding.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nodes")
public class NodeEntity {

    @Id
    private String id;

    private String name;

    private String type;

    private int floor;

    public NodeEntity() {
    }

    public NodeEntity(
            String id,
            String name,
            String type,
            int floor) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.floor = floor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}