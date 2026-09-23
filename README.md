# Indoor Wayfinding Backend

A Spring Boot application for indoor navigation and route planning across a multi-floor campus. The project models connected indoor spaces as a graph, calculates valid shortest paths with Dijkstra's algorithm, and exposes a REST API protected by HTTP Basic Authentication.

## Overview

This backend supports:

- routing between indoor locations
- wheelchair-aware route filtering
- nearest POI lookup for washrooms, water stations, and exits
- multi-stop journey planning
- route distance and estimated walking time calculation
- validation for invalid or unreachable inputs
- graph backup and recovery support
- interactive API documentation through Swagger UI

The application uses an in-memory campus graph initialized at startup, with additional support for graph backup, caching, metrics, and monitoring.

### Persistence note

The current prototype performs routing against an in-memory graph for fast path calculation.

JPA entities and repositories are included as the persistence foundation for storing nodes, edges, closures, and POIs in a relational database. The runtime routing graph is currently initialized in memory rather than being loaded from the database on every request.

## Repository

GitHub: https://github.com/mynkpandey/indoor-wayfinding

## Features

- Graph-based indoor route planning
- Dijkstra shortest-path routing
- Multi-floor node and edge modeling
- Wheelchair-accessible route filtering
- Congestion-aware edge weighting
- Time-based closure handling on edges
- Nearest POI search for:
  - WASHROOM
  - WATER
  - EXIT
- Multi-stop route planning
- Input validation and structured error responses
- Start-equals-destination handling
- Unreachable route detection
- Spring Security basic authentication
- Swagger/OpenAPI interactive API documentation
- Caffeine cache support for route data
- Actuator health and metrics endpoints
- Graph backup and recovery support
- Prometheus/Grafana monitoring integration
- JUnit tests for routing behavior

## Technology Stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Springdoc OpenAPI / Swagger UI
- Spring Cache with Caffeine
- H2 Database
- Spring Boot Actuator
- Micrometer + Prometheus
- Grafana
- Maven
- JUnit 5

## Project Architecture

The application follows a layered backend structure:

- Controller layer: exposes REST endpoints
- Service layer: handles route calculations and response creation
- Algorithm layer: contains the in-memory graph and Dijkstra implementation
- Persistence layer: JPA entities and repositories for campus data storage
- Security layer: protects endpoints and enforces Basic Auth
- Monitoring layer: metrics and health endpoints for Prometheus/Grafana

## Security and Authentication

The app uses HTTP Basic Authentication for protected endpoints.

### Security rules

- `/api/health` is public
- `/api/health/recovery` is protected
- `/actuator/health` is public
- `/actuator/prometheus` is public
- `/swagger-ui/**`, `/swagger-ui.html`, and `/v3/api-docs/**` are public
- all other endpoints require authentication
- unauthorized requests return HTTP 401 with a JSON response

Example unauthorized response:

```json
{
  "status": "UNAUTHORIZED",
  "message": "Authentication is required."
}
```

### Demo authentication

The application uses HTTP Basic Authentication for protected endpoints.

For local demonstration, credentials are configured in the application's security configuration.

> In a production deployment, credentials should be stored securely using environment variables, a database-backed identity provider, or OAuth2/JWT rather than hardcoded credentials.

## Swagger / OpenAPI

Interactive API documentation is available through Swagger UI:

http://localhost:8080/swagger-ui/index.html

Swagger provides interactive documentation and testing for the REST endpoints. Protected endpoints can be tested using HTTP Basic Authentication.

The OpenAPI configuration is defined in `OpenApiConfig.java` and includes a Basic Auth security scheme for all documented APIs.

## Sample Campus Graph

The app initializes a default campus graph at startup with the following nodes:

- N1 - Reception
- N2 - Corridor A
- N3 - Stairs
- N4 - Lift
- N5 - Floor 2 Corridor
- N6 - Meeting Room 4B
- N7 - Washroom - Floor 1
- N8 - Water Station - Floor 2
- N9 - Main Exit

The sample graph includes:

- N1 ↔ N2
- N2 ↔ N3 (stairs; not wheelchair accessible)
- N2 ↔ N4 (lift; wheelchair accessible, congested)
- N3 ↔ N5
- N4 ↔ N5
- N5 ↔ N6 (time-closed during a scheduled window)
- N2 ↔ N7
- N5 ↔ N8
- N1 ↔ N9

This sample campus is used to test route behavior without requiring a real map backend.

## Routing Logic

The project uses Dijkstra's shortest-path algorithm to compute the lowest-cost route between two points.

### Cost model

Route cost is based on:

- physical distance
- congestion multiplier
- wheelchair accessibility rules
- edge closure times

The algorithm uses a Java `PriorityQueue` to always select the next node with the smallest known cost.

### Complexity

For a graph with V vertices and E edges:

- Time: O((V + E) log V)
- Space: O(V + E)

Multi-stop route planning uses a greedy nearest-stop strategy, which is practical for a small number of stops but is not a full traveling salesman optimizer.

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| GET | `/api/health` | Health check | No |
| POST | `/api/routes` | Find shortest route | Yes |
| GET | `/api/poi/nearest` | Find nearest POI | Yes |
| POST | `/api/routes/multi-stop` | Multi-stop route | Yes |
| GET | `/api/health/recovery` | Graph recovery status | Yes |

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

### Recovery health

```http
GET /api/health/recovery
```

Example response:

```json
{
  "status": "UP",
  "backupAvailable": true,
  "message": "Campus graph backup is available."
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

The API returns structured JSON responses for invalid, missing, or impossible route requests.

### Invalid request

```json
{
  "status": "INVALID_REQUEST",
  "message": "Destination does not exist."
}
```

### Unreachable route

```json
{
  "status": "NO_ROUTE",
  "message": "No route found between the selected locations."
}
```

### Same start and destination

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

## Testing

The project includes automated JUnit tests covering:

- Dijkstra shortest-path routing
- wheelchair accessibility
- unreachable destinations
- congestion-aware routing
- time-based closures
- successful route requests
- invalid start/destination handling
- start equals destination
- controller authentication
- controller validation

Latest test result:

```text
Tests run: 15
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Run:

```bash
./mvnw clean test
```

## Monitoring and Cache

The application exposes Spring Boot Actuator endpoints for health and metrics and uses Spring Cache with Caffeine to reduce repeated route computation.

### Monitoring flow

```text
Spring Boot
   ↓
Micrometer / Actuator
   ↓
Prometheus
   ↓
Grafana
```

The project includes:

- health endpoint exposure
- Prometheus metrics export
- caching for repeated route lookups
- graph backup and recovery diagnostics

### Actuator configuration

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.info.env.enabled=true

info.app.name=Indoor Wayfinding Backend
info.app.description=Indoor navigation and route planning system
info.app.version=1.0.0
```

### Caching trade-off

Route results are cached using Caffeine with a short TTL to reduce repeated Dijkstra calculations.

Because edge closures are time-dependent, cached routes may remain valid until the cache entry expires. The prototype uses a 5-minute TTL as a balance between performance and route freshness.

A production system could invalidate affected route entries whenever closure schedules change.

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
│   │   │   ├── GraphBackupInitializer.java
│   │   │   ├── OpenApiConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── MultiStopController.java
│   │   │   ├── PoiController.java
│   │   │   └── RouteController.java
│   │   ├── entity/
│   │   │   ├── NodeEntity.java
│   │   │   ├── EdgeEntity.java
│   │   │   ├── ClosureEntity.java
│   │   │   └── PoiEntity.java
│   │   ├── model/
│   │   │   ├── Edge.java
│   │   │   ├── ErrorResponse.java
│   │   │   ├── MultiStopRequest.java
│   │   │   ├── Node.java
│   │   │   ├── RouteRequest.java
│   │   │   └── RouteSegment.java
│   │   ├── repository/
│   │   │   ├── NodeRepository.java
│   │   │   ├── EdgeRepository.java
│   │   │   ├── ClosureRepository.java
│   │   │   └── PoiRepository.java
│   │   └── service/
│   │       ├── GraphRecoveryService.java
│   │       └── RouteService.java
│   └── resources/
│       └── application.properties
├── test/
│   └── java/com/movieinsync/wayfinding/
│       ├── DijkstraServiceTest.java
│       ├── IndoorWayfindingApplicationTests.java
│       ├── RouteServiceTest.java
│       └── RouteControllerTest.java
├── docs/
│   └── ER-DIAGRAM.md
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Running the Project

From the project root, run:

```bash
./mvnw spring-boot:run
```

### Development resources

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health endpoint: http://localhost:8080/api/health

## Notes

- The campus graph is initialized in-memory during startup.
- Graph backup is created automatically via the startup initializer.
- The routing API is protected by HTTP Basic Authentication.
- Swagger docs show the expected request/response structure and allow interactive testing.
