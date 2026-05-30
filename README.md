# Global Class Offering Booking System

## Overview
A production-ready backend service for a global live-learning platform. This system allows teachers to create course offerings and sessions across different timezones, while enabling parents to book offerings for their children with built-in conflict detection and concurrency handling.

## Features
- **Timezone-Aware Scheduling**: Teachers create sessions in their timezone; parents view them in theirs.
- **Atomic Bookings**: Bookings apply to the entire offering.
- **Overlap Prevention**: Intelligent conflict detection prevents parents from booking overlapping sessions.
- **High Concurrency Support**: Robust handling of simultaneous booking requests using database-level locking and transaction management.
- **Idempotency**: Safe retries for booking requests.
- **Capacity & Waitlist**: Automated management of offering capacity and student waitlisting.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.3.5
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Migration**: Liquibase
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Containerization**: Docker & Docker Compose
- **Utilities**: Lombok

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- Docker and Docker Compose

### Environment Variables Required
The application can be configured using the following environment variables:

| Variable      | Description                      | Default Value                                |
|---------------|----------------------------------|----------------------------------------------|
| `DB_URL`      | PostgreSQL JDBC URL              | `jdbc:postgresql://localhost:5432/undoschool_db` |
| `DB_USERNAME` | Database username                | `postgres`                                   |
| `DB_PASSWORD` | Database password                | `postgres`                                   |
| `SERVER_PORT` | Port on which the server runs    | `8080`                                       |

### Running Locally

#### Option 1: Docker Compose (Recommended)
This is the easiest way to run the application as it sets up both the application and the PostgreSQL database.

1. Navigate to the project directory:
   ```bash
   cd class-booking-system
   ```
2. Build the project:
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. Start the services:
   ```bash
   docker-compose up --build
   ```

#### Option 2: Maven
1. Ensure a PostgreSQL instance is running with a database named `undoschool_db`.
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Architecture
The project follows a hexagonal/layered architecture:
- **Web Layer**: REST Controllers handling HTTP requests and DTO mapping.
- **Service Layer**: Business logic, conflict detection, and transaction management.
- **Persistence Layer**: Spring Data repositories for PostgreSQL interaction.

## API Documentation
Once running, the interactive Swagger UI is available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Key Endpoints Overview
| Role       | Endpoint                                     | Method | Description                                |
|------------|----------------------------------------------|--------|--------------------------------------------|
| Management | `/api/v1/courses`                            | POST   | Create a new course                        |
| Management | `/api/v1/teachers`                           | POST   | Register a new teacher                     |
| Management | `/api/v1/parents`                            | POST   | Register a new student/parent              |
| Teacher    | `/api/v1/teachers/offerings`                 | POST   | Create a course offering (Header: `X-Teacher-Id`) |
| Teacher    | `/api/v1/teachers/offerings/{id}/sessions` | POST   | Add sessions to an offering (Header: `X-Teacher-Id`) |
| Parent     | `/api/v1/parents/offerings`                  | GET    | List all available offerings               |
| Parent     | `/api/v1/parents/bookings`                   | POST   | Book an offering (Header: `X-Student-Id`)  |

## Database Schema Overview
The database consists of the following main tables:
- **`courses`**: Stores base course information.
- **`teachers`**: Teacher details.
- **`students`**: Student details.
- **`offerings`**: Instances of a course taught by a teacher, with capacity limits.
- **`sessions`**: Time slots associated with an offering.
- **`bookings`**: Student booking records (CONFIRMED/WAITLISTED).

## Concurrency & Timezone Handling

### Concurrency
- **Pessimistic Locking**: Uses `PESSIMISTIC_WRITE` on Offering records during booking to prevent race conditions.
- **Idempotency**: Requires a unique `idempotency_key` for booking requests to handle network retries safely.

### Timezones
- **Storage**: Uses `TIMESTAMPTZ` in PostgreSQL to store absolute points in time.
- **Application**: Uses `OffsetDateTime` in Java to preserve client offset information and ensure consistency across regions.

## CI/CD Pipeline
The project includes a GitHub Actions pipeline that:
- Automatically runs unit tests on every push and pull request.
- Builds a Docker image on merges to the `main` branch.
- Pushes the image to DockerHub.

### GitHub Secrets Required:
- `DOCKERHUB_USERNAME`: Your DockerHub username.
- `DOCKERHUB_TOKEN`: Your DockerHub personal access token.

## Dockerization
The official image is available on DockerHub: `mahangarerohan1203/class-booking-system`

### Running with Docker Image
```bash
docker run -p 8080:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/undoschool_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  mahangarerohan1203/class-booking-system:latest
```
