package com.movieinsync.wayfinding.model;

import jakarta.validation.constraints.NotBlank;

public class RouteRequest {

    @NotBlank(message = "Start location is required.")
    private String start;

    @NotBlank(message = "Destination is required.")
    private String destination;

    private boolean wheelchairAccessible;

    public RouteRequest() {}

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }
}