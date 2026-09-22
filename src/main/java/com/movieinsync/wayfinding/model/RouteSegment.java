package com.movieinsync.wayfinding.model;

public class RouteSegment {

    private String from;
    private String to;
    private double distance;
    private boolean wheelchairAccessible;

    public RouteSegment(
            String from,
            String to,
            double distance,
            boolean wheelchairAccessible) {

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }
}