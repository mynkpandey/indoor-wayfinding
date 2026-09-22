# Indoor Wayfinding Backend

This project is a Spring Boot backend for indoor navigation in a multi-floor building. It models the campus as a weighted graph and calculates shortest valid routes using Dijkstra's algorithm.

## Project Summary

The application is designed to help users:

- find the shortest route between two indoor locations
- avoid inaccessible routes for wheelchair users
- find the nearest washroom, water station, or exit
- plan a route with multiple stops
- receive route details including distance and estimated walking time

The system currently uses an in-memory graph for route calculation and includes JPA entities and repositories for future persistence and database-backed campus data.

## Repository

GitHub: https://github.com/mynkpandey/indoor-wayfinding

## Features

- Weighted graph-based indoor navigation
- Dijkstra shortest-path calculation
- Multi-floor node modeling
- Wheelchair-accessible route support
- Congestion-based edge weighting
- Time-based edge closure handling
- Nearest POI lookup
  - WASHROOM
  - WATER
  - EXIT
- Multi-stop path planning
- Start equals destination handling
- Invalid request validation
- Unreachable route handling
- Spring Boot Actuator health endpoint
- Java unit tests for routing logic

## Technology Stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- Maven
- JUnit 5

## Architecture

The application follows a simple layered structure:

- Controller layer: exposes REST endpoints
- Service layer: handles business logic and route response formatting
- Algorithm layer: contains the graph and shortest-path logic
- Persistence layer: JPA entities and repositories for campus data

The current route engine runs on a custom in-memory graph defined in the algorithm package, while the entity and repository packages provide database-ready models.

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
│   │   │   └── DatabaseSeeder.java
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
│   │   ├── model/
│   │   │   ├── Edge.java
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
│   │   │   └── RouteService.java
│   │   └── IndoorWayfindingApplication.java
│   └── resources/
│       └── application.properties
├── test/
│   └── java/com/movieinsync/wayfinding/
│       ├── DijkstraServiceTest.java
│       └── IndoorWayfindingApplicationTests.java
└── docs/
    └── ER-DIAGRAM.md
```

## Data Model

The campus is represented with a graph of nodes and edges.

### Node

Each node represents a place in the building, such as:

- Reception
- Corridor
- Lift
- Stairs
- Meeting Room
- Washroom
- Water Station
- Exit

Example node values used in the seeded campus graph:

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

Each edge connects two nodes and carries route information such as:

- source and destination node IDs
- physical distance
- wheelchair accessibility flag
- congestion multiplier
- optional closure time range

## Routing Logic

The project uses Dijkstra's shortest-path algorithm to compute the lowest-cost route between locations.

## Complexity Analysis

Dijkstra's algorithm uses an adjacency-list graph and Java's
PriorityQueue.

Time complexity:

O((V + E) log V)

where:

- V = number of nodes
- E = number of edges

Space complexity:

O(V + E)

The PriorityQueue is used to efficiently select the next node with the
smallest known route cost.

For repeated multi-stop routing with k stops, the approximate
complexity is:

O(k × (V + E) log V)

For larger campuses with 1000+ nodes, route caching and precomputation
of frequently requested routes can reduce repeated computation.

However, dynamic congestion and time-based closures create a trade-off
between caching performance and route-data freshness.

### Route cost

The effective route cost is based on:

- distance between nodes
- congestion multiplier
- whether the route is wheelchair accessible
- whether the edge is closed at the current time

When a route is calculated, any edge that is closed at the current time is skipped. If wheelchair routing is enabled, inaccessible paths such as stairs are ignored.

## API Endpoints

### 1. Health Check

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

### 2. Find Route

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
  "path": [
    "Reception",
    "Corridor A",
    "Stairs",
    "Floor 2 Corridor",
    "Meeting Room 4B"
  ],
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
    },
    {
      "from": "Stairs",
      "to": "Floor 2 Corridor",
      "distance": 25.0,
      "wheelchairAccessible": false
    },
    {
      "from": "Floor 2 Corridor",
      "to": "Meeting Room 4B",
      "distance": 15.0,
      "wheelchairAccessible": true
    }
  ],
  "totalDistance": 75.0,
  "estimatedTimeMinutes": 0.9
}
```

### 3. Find Closest POI

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

### 4. Multi-Stop Route

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
  "path": ["Reception", "Corridor A", "Washroom - Floor 1", "Corridor A",
    "Lift",
    "Floor 2 Corridor", "Water Station - Floor 2"],
  "totalDistance": 92.0,
  "stopsVisited": ["N7", "N8"]
}
```
## Error Handling

The application handles several invalid or impossible routing conditions without crashing.
Examples include:

### Invalid start location

```json
{
  "status": "INVALID_REQUEST",
  "message": "Start location does not exist."
}
```
### Invalid destination

```json
{
  "status": "INVALID_REQUEST",
  "message": "Destination does not exist."
}
```
### Unreachable destination

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
  "path": [
    "Reception"
  ],
  "segments": [],
  "totalDistance": 0.0,
  "estimatedTimeMinutes": 0.0
}
```
## Sample Campus Setup

The application initializes a sample campus graph in `DataInitializer.java` with these route patterns:

- N1 ↔ N2
- N2 ↔ N3 (stairs, not wheelchair accessible)
- N2 ↔ N4 (lift, wheelchair accessible but congested)
- N3 ↔ N5
- N4 ↔ N5
- N5 ↔ N6 (closed at specific time windows)
- N2 ↔ N7 (washroom)
- N5 ↔ N8 (water station)
- N1 ↔ N9 (exit)

This sample campus is useful for testing routing logic without requiring an external map service or database import.

## Database and Persistence

The project also includes JPA entity and repository classes for campus data management:

- NodeEntity
- EdgeEntity
- ClosureEntity
- PoiEntity
- NodeRepository
- EdgeRepository
- ClosureRepository
- PoiRepository

The seeded data in `DatabaseSeeder.java` loads initial campus nodes into the H2 database.

## ER Diagram

The project includes an ER diagram describing the planned relational persistence model.

The database model contains:

### Database Relationship Structure

```text
BUILDINGS
    │
    └── NODES
          │
          ├── EDGES
          │      └── CLOSURES
          │
          └── POIS
```

### See:
```text
docs/ER-DIAGRAM.md
```
for the detailed database design and relationships.


## Running the Project

### Prerequisites

- Java 17+
- Maven

### Run locally

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

### Run tests

```bash
./mvnw test
```

## Notes

This project is a backend-focused proof of concept for indoor wayfinding. It demonstrates graph-based pathfinding, route filtering, POI discovery, and campus data modeling in a Spring Boot application.

The current implementation uses an in-memory graph for runtime routing, while the database layer is prepared to support more persistent and scalable campus data in the future.

## Assumptions and Trade-offs

- Edge weights are non-negative.
- Walking speed is assumed to be 1.4 metres/second.
- Congestion is represented using a multiplier.
- Closed edges cannot be used during their closure period.
- Wheelchair routing excludes inaccessible edges.
- The current routing graph is maintained in memory.
- H2/JPA provides the persistence model but is not currently the
  runtime source for Dijkstra's graph.
- Multi-stop routing uses a greedy nearest-stop heuristic rather than
  an exact TSP solution.

### In-Memory Graph

Advantages:

- Fast route calculation
- Simple graph traversal
- Efficient Dijkstra execution

Trade-off:

- Campus data changes require graph updates or reloading.

### Database Persistence

Advantages:

- Persistent campus data
- Easier future administration and updates
- Suitable for production data management

Trade-off:

- Database access introduces additional overhead.

### Multi-Stop Routing

The greedy approach is simple and efficient for a small number of
stops, but it does not guarantee the globally optimal ordering of all
stops.

## Authentication

The API uses Spring Security with HTTP Basic Authentication.

- `/api/health` is publicly accessible.
- Other API endpoints require authentication.
- Authentication is currently implemented using an in-memory demo user.
- Stateless session management is used.
- Unauthorized requests return HTTP 401 with a structured JSON response.

For production deployment, database-backed users with hashed passwords or JWT/OAuth2 should be used.

## Error & Exception Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

Handled cases include:

- Missing required request fields
- Invalid route requests
- Unreachable destinations
- Authentication failures
- Unexpected server-side exceptions

Validation errors return HTTP 400 with a structured response:

```json
{
  "status": "INVALID_REQUEST",
  "message": "Destination is required.",
  "timestamp": "..."
}


#### 3. Failure Handling

```markdown
## Failure Handling

The system is designed to fail gracefully instead of crashing.

Examples:

- Invalid start/destination → clear error response
- Start equals destination → valid zero-distance response
- Unreachable destination → `NO_ROUTE`
- Time-based closure → alternative route or `NO_ROUTE`
- Wheelchair-inaccessible path → filtered from routing
- Missing authentication → HTTP 401
- Invalid request data → HTTP 400
- Unexpected server error → HTTP 500 with centralized handling