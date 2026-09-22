# Indoor Wayfinding - Database / ER Design

## Overview

The current prototype stores the navigation graph in memory for fast
algorithm execution.

For production deployment, the campus graph can be persisted in a
relational database.

## Database Tables

### 1. BUILDINGS

| Column | Type | Description |
|---|---|---|
| id | BIGINT PK | Building identifier |
| name | VARCHAR | Building name |

### 2. NODES

| Column | Type | Description |
|---|---|---|
| id | VARCHAR PK | Unique node identifier |
| name | VARCHAR | Location name |
| type | VARCHAR | ROOM, CORRIDOR, STAIRS, LIFT, etc. |
| floor | INT | Floor number |
| building_id | BIGINT FK | Building containing the node |

### 3. EDGES

| Column | Type | Description |
|---|---|---|
| id | BIGINT PK | Edge identifier |
| from_node_id | VARCHAR FK | Starting node |
| to_node_id | VARCHAR FK | Destination node |
| distance_m | DOUBLE | Physical distance in metres |
| wheelchair_accessible | BOOLEAN | Whether wheelchair users can use the edge |
| congestion_multiplier | DOUBLE | Dynamic congestion factor |

### 4. CLOSURES

| Column | Type | Description |
|---|---|---|
| id | BIGINT PK | Closure identifier |
| edge_id | BIGINT FK | Affected edge |
| closed_from | TIME | Closure start time |
| closed_until | TIME | Closure end time |

### 5. POIS

| Column | Type | Description |
|---|---|---|
| id | BIGINT PK | POI identifier |
| node_id | VARCHAR FK | Node representing the POI |
| type | VARCHAR | WASHROOM, WATER, EXIT, etc. |

## ER Diagram

```mermaid
erDiagram

    BUILDINGS ||--o{ NODES : contains

    NODES ||--o{ EDGES : "from node"

    NODES ||--o{ EDGES : "to node"

    EDGES ||--o{ CLOSURES : has

    NODES ||--o| POIS : represents

    BUILDINGS {
        BIGINT id PK
        VARCHAR name
    }

    NODES {
        VARCHAR id PK
        VARCHAR name
        VARCHAR type
        INT floor
        BIGINT building_id FK
    }

    EDGES {
        BIGINT id PK
        VARCHAR from_node_id FK
        VARCHAR to_node_id FK
        DOUBLE distance_m
        BOOLEAN wheelchair_accessible
        DOUBLE congestion_multiplier
    }

    CLOSURES {
        BIGINT id PK
        BIGINT edge_id FK
        TIME closed_from
        TIME closed_until
    }

    POIS {
        BIGINT id PK
        VARCHAR node_id FK
        VARCHAR type
    }