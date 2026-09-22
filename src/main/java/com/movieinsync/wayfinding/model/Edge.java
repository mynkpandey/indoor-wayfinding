package com.movieinsync.wayfinding.model;

import java.time.LocalTime;

public class Edge {

    private String from;
    private String to;
    private double distance;
    private boolean wheelchairAccessible;
    private boolean closed;
    private double congestionMultiplier;

    private LocalTime closedFrom;
    private LocalTime closedUntil;

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

    public Edge(
            String from,
            String to,
            double distance,
            boolean wheelchairAccessible,
            boolean closed,
            double congestionMultiplier,
            LocalTime closedFrom,
            LocalTime closedUntil) {

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.wheelchairAccessible = wheelchairAccessible;
        this.closed = closed;
        this.congestionMultiplier = congestionMultiplier;
        this.closedFrom = closedFrom;
        this.closedUntil = closedUntil;
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

    public LocalTime getClosedFrom() {
        return closedFrom;
    }

    public LocalTime getClosedUntil() {
        return closedUntil;
    }

    public double getEffectiveDistance() {
        return distance * congestionMultiplier;
    }

    public boolean isClosedAt(LocalTime time) {

        if (closed) {
            return true;
        }

        if (closedFrom == null || closedUntil == null) {
            return false;
        }

        return !time.isBefore(closedFrom)
                && time.isBefore(closedUntil);
    }
}