# Indoor Wayfinding Backend

A Spring Boot backend for indoor navigation in a multi-floor building. The application models the campus as a graph of connected nodes and calculates shortest valid routes using Dijkstra's algorithm.

## Overview

This project is a backend-focused indoor navigation system designed to:

- find the shortest route between indoor locations
- support wheelchair-friendly routing
- calculate route distance and estimated walking time
- locate the nearest POI such as a washroom, water station, or exit
- plan multi-stop journeys
- handle invalid inputs and unreachable destinations cleanly

The runtime routing engine uses an in-memory graph, while the project also includes JPA entities, repositories, and a seeded H2 database model.

## Repository

GitHub: https://github.com/mynkpandey/indoor-wayfinding

## Features

- Graph-based indoor route planning
- Dijkstra shortest-path computation
- Multi-floor node and edge modeling
- Wheelchair-accessible route filtering
- Congestion-aware edge weighting
- Time-based closure handling per edge
- Nearest POI search for:
  - WASHROOM
  - WATER
  - EXIT
- Multi-stop route planning
- Input validation and structured error responses
- Start equals destination handling
- Unreachable route detection
- Spring Security basic authentication
- Caffeine cache support for route data
- Actuator health and metrics endpoints
- Graph backup and recovery support
- JUnit tests for routing behavior

## Technology Stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Cache with Caffeine
- H2 Database
- Spring Boot Actuator
- Maven
- JUnit 5

## Authentication

The application uses HTTP Basic Authentication.

### Security rules

- `/api/health` is public
- `/actuator/health` is public
- all other endpoints require authentication
- unauthorized requests return HTTP 401 with a structured JSON response

Example unauthorized response:

```json
{
  "status": "UNAUTHORIZED",
  "message": "Authentication is required."
}
```

## Architecture

The project is organized in a layered backend structure:

- Controller layer: exposes REST endpoints
- Service layer: handles route logic and response creation
- Algorithm layer: contains the in-memory graph and Dijkstra implementation
- Persistence layer: JPA entity and repository classes for database-backed campus data
- Security layer: validates authenticated access to protected endpoints

## Project Structure

```text
src/
├── main/
│   ├── java/com/movieinsync/wayfinding/
│   │   ├── algorithm/
│   │   │   ├── DijkstraService.java
│   │   │   └── Graph.java
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   ├── DatabaseSeeder.java
│   │   │   ├── GraphBackupInitializer.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── RouteController.java
│   │   │   ├── PoiController.java
│   │   │   └── MultiStopController.java
│   │   ├── entity/
│   │   │   ├── ClosureEntity.java
│   │   │   ├── EdgeEntity.java
│   │   │   ├── NodeEntity.java
│   │   │   └── PoiEntity.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── RouteNotFoundException.java
│   │   ├── model/
│   │   │   ├── Edge.java
│   │   │   ├── ErrorResponse.java
│   │   │   ├── MultiStopRequest.java
│   │   │   ├── Node.java
│   │   │   ├── RouteRequest.java
│   │   │   └── RouteSegment.java
│   │   ├── repository/
│   │   │   ├── ClosureRepository.java
│   │   │   ├── EdgeRepository.java
│   │   │   ├── NodeRepository.java
│   │   │   └── PoiRepository.java
│   │   ├── service/
│   │   │   ├── GraphDatabaseLoader.java
│   │   │   ├── GraphRecoveryService.java
│   │   │   └── RouteService.java
│   │   └── IndoorWayfindingApplication.java
│   └── resources/
│       └── application.properties
├── test/
│   └── java/com/movieinsync/wayfinding/
│       ├── DijkstraServiceTest.java
│       └── IndoorWayfindingApplicationTests.java
├── docs/
│   └── ER-DIAGRAM.md
└── pom.xml
```

## Campus Data Model

The app represents a campus as a graph made of nodes and edges.

### Node

A node is an indoor location such as:

- Reception
- Corridor
- Lift
- Stairs
- Meeting Room
- Washroom
- Water Station
- Exit

Example node definitions used by the sample campus:

- N1 - Reception
- N2 - Corridor A
- N3 - Stairs
- N4 - Lift
- N5 - Floor 2 Corridor
- N6 - Meeting Room 4B
- N7 - Washroom - Floor 1
- N8 - Water Station - Floor 2
- N9 - Main Exit

### Edge

An edge connects two nodes and stores route metadata such as:

- source and target node IDs
- travel distance
- wheelchair accessibility
- congestion multiplier
- optional closure time window

## Routing Logic

The project uses Dijkstra's shortest-path algorithm to find the lowest-cost route between two points.

### Cost model

The route cost is based on:

- physical distance
- congestion multiplier
- wheelchair accessibility rules
- edge closure times

The implementation uses a Java `PriorityQueue` to always select the next node with the smallest known cost.

### Complexity

For a graph with V vertices and E edges:

- Time: O((V + E) log V)
- Space: O(V + E)

Multi-stop route planning uses a greedy nearest-stop approach, which is practical for small stop sets but is not an exact traveling salesman solution.

## API Endpoints

### Health check

```http
GET /api/health
```

Example response:

```json
{
  "status": "UP",
  "service": "Indoor Wayfinding Backend"
}
```

### Find route

```http
POST /api/routes
```

Request body:

```json
{
  "start": "N1",
  "destination": "N6",
  "wheelchairAccessible": false
}
```

Example response:

```json
{
  "status": "SUCCESS",
  "path": ["Reception", "Corridor A", "Stairs", "Floor 2 Corridor", "Meeting Room 4B"],
  "segments": [
    {
      "from": "Reception",
      "to": "Corridor A",
      "distance": 20.0,
      "wheelchairAccessible": true
    },
    {
      "from": "Corridor A",
      "to": "Stairs",
      "distance": 15.0,
      "wheelchairAccessible": false
    }
  ],
  "totalDistance": 75.0,
  "estimatedTimeMinutes": 0.9
}
```

### Find nearest POI

```http
GET /api/poi/nearest?start=N1&type=WASHROOM
```

Example response:

```json
{
  "status": "SUCCESS",
  "poi": "Washroom - Floor 1",
  "type": "WASHROOM",
  "path": ["Reception", "Corridor A", "Washroom - Floor 1"],
  "distance": 28.0
}
```

### Multi-stop routing

```http
POST /api/routes/multi-stop
```

Request body:

```json
{
  "start": "N1",
  "stops": ["N7", "N8"],
  "wheelchairAccessible": true
}
```

Example response:

```json
{
  "status": "SUCCESS",
  "path": ["Reception", "Corridor A", "Washroom - Floor 1", "Lift", "Floor 2 Corridor", "Water Station - Floor 2"],
  "totalDistance": 92.0,
  "stopsVisited": ["N7", "N8"]
}
```

## Error Handling

The app returns structured JSON responses for invalid or impossible route requests.

### Examples

#### Invalid request

```json
{
  "status": "INVALID_REQUEST",
  "message": "Destination does not exist."
}
```

#### Unreachable route

```json
{
  "status": "NO_ROUTE",
  "message": "No route found between the selected locations."
}
```

#### Same start and destination

```json
{
  "status": "SUCCESS",
  "message": "Start and destination are the same.",
  "path": ["Reception"],
  "segments": [],
  "totalDistance": 0.0,
  "estimatedTimeMinutes": 0.0
}
```

## Sample Campus Setup

The app initializes a sample campus graph in `DataInitializer.java` with the following routing paths:

- N1 ↔ N2
- N2 ↔ N3 (stairs, not wheelchair accessible)
- N2 ↔ N4 (lift, wheelchair accessible, congested)
- N3 ↔ N5
- N4 ↔ N5
- N5 ↔ N6 (closed during a scheduled time window)
- N2 ↔ N7
- N5 ↔ N8
- N1 ↔ N9

This fake campus is used to test route behavior without needing a real map backend.

## Persistence and Database Design

The project includes JPA entities and repository classes for a relational database model:

- `NodeEntity`
- `EdgeEntity`
- `ClosureEntity`
- `PoiEntity`
- `NodeRepository`
- `EdgeRepository`
- `ClosureRepository`
- `PoiRepository`

The ER diagram is documented in [docs/ER-DIAGRAM.md](docs/ER-DIAGRAM.md).

## Monitoring and Cache

The application exposes Spring Boot Actuator endpoints for health and metrics, and uses Spring Cache with Caffeine to reduce repeated route computation.

### Actuator configuration

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.info.env.enabled=true

info.app.name=Indoor Wayfinding Backend
info.app.description=Indoor navigation and route planning system
info.app.version=1.0.0
```

### Route cache configuration

```properties
spring.cache.type=caffeine
spring.cache.cache-names=routes
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=5m
```

### Monitoring endpoints

```text
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### Route caching

The application caches route results using a key derived from:

- start node
- destination node
- wheelchair requirement

Example cache key:

```text
N1-N6-false
```

This helps avoid recomputing the same path for repeated requests when the campus graph is unchanged. The trade-off is that cached responses can become stale if graph data, closures, or congestion values change. In this project, the 5-minute expiration window strikes a practical balance between fast repeated lookups and route freshness.

## Graph Backup and Recovery

The app includes a backup mechanism for the in-memory graph so the campus structure can be restored if needed.

Components:

- `GraphBackupInitializer`
- `GraphRecoveryService`
- `Graph.deepCopy()`

### Backup behavior

`createBackup()` creates an independent deep copy of the current campus graph.

### Recovery checks

`isBackupAvailable()` checks whether a recovery backup exists before attempting restoration.

### Restore behavior

`recoverGraph()` restores a fresh copy of the backup graph for fallback or recovery scenarios.

This is a fault-tolerance feature for the runtime graph used during route calculations. It is useful for protecting graph state in a prototype environment, although the active routing engine remains in-memory rather than database-driven.

## Running the Project

### Prerequisites

- Java 17+
- Maven

### Start the application

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

### Run tests

```bash
./mvnw test
```

## Notes

This project is a backend prototype for an indoor wayfinding system. It demonstrates graph traversal, constrained shortest-path routing, POI detection, authentication, caching, and monitoring in a single Spring Boot service.

The current implementation uses an in-memory graph as the active routing source, while the JPA layer provides model support for future persistence and database-driven campus data.