package com.movieinsync.wayfinding.model;

public class Edge {

    private String from;
    private String to;
    private double distance;

    private boolean wheelchairAccessible;
    private boolean closed;

    private double congestionMultiplier;

    public Edge(
            String from,
            String to,
            double distance,
            boolean wheelchairAccessible,
            boolean closed,
            double congestionMultiplier) {

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.wheelchairAccessible = wheelchairAccessible;
        this.closed = closed;
        this.congestionMultiplier = congestionMultiplier;
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

    public boolean isClosed() {
        return closed;
    }

    public double getCongestionMultiplier() {
        return congestionMultiplier;
    }

    public double getEffectiveDistance() {
        return distance * congestionMultiplier;
    }
}