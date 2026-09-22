package com.movieinsync.wayfinding.model;

import java.util.List;

public class MultiStopRequest {

    private String start;
    private List<String> stops;
    private boolean wheelchairAccessible;

    public MultiStopRequest() {
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }
}