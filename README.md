# Indoor Wayfinding Backend

A Spring Boot backend for indoor navigation inside a multi-floor campus.

The system represents the campus as a weighted graph and uses Dijkstra's
shortest-path algorithm with a Java PriorityQueue to calculate routes.

## Features

- Graph-based indoor campus representation
- Multi-floor navigation
- Dijkstra shortest-path routing
- PriorityQueue-based route calculation
- Wheelchair-accessible routing
- Congestion-aware route weights
- Time-based route closures
- Estimated walking time
- Nearest POI search
  - Washroom
  - Water station
  - Exit
- Multi-stop route planning
- Invalid input handling
- Unreachable destination handling
- Start-equals-destination handling
- Automated unit tests
- Spring Boot Actuator health endpoint

## Technology Stack

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- JUnit
- Postman

## Project Structure

```text
src/main/java/com/movieinsync/wayfinding
│
├── algorithm
│   ├── Graph.java
│   └── DijkstraService.java
│
├── config
│   └── DataInitializer.java
│
├── controller
│   ├── HealthController.java
│   ├── RouteController.java
│   ├── PoiController.java
│   └── MultiStopController.java
│
└── model
    ├── Node.java
    ├── Edge.java
    ├── RouteRequest.java
    └── MultiStopRequest.java