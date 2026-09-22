package com.movieinsync.wayfinding.model;

public class Node {

    private String id;
    private String name;
    private String type;
    private int floor;

    public Node(String id, String name, String type, int floor) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.floor = floor;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }
}