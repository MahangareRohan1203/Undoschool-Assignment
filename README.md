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
- **Framework**: Spring Boot 3.3.x
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA
- **Migration**: Liquibase
- **Testing**: JUnit 5, Mockito, Testcontainers

## Getting Started

### Prerequisites
- JDK 17
- Docker (for PostgreSQL)
- Maven 3.8+

### Running Locally
1. Start the database:
   ```bash
   docker compose up -d
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Architecture
The project follows a hexagonal/layered architecture:
- **Web Layer**: REST Controllers handling HTTP requests and DTO mapping.
- **Service Layer**: Business logic, conflict detection, and transaction management.
- **Persistence Layer**: Spring Data repositories for PostgreSQL interaction.

## API Documentation
Once running, the Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

## CI/CD Pipeline
The project includes a GitHub Actions pipeline that:
- Automatically runs unit tests on every push and pull request.
- Builds a Docker image on merges to the `main` branch.
- Pushes the image to DockerHub.

### GitHub Secrets Required:
- `DOCKERHUB_USERNAME`: Your DockerHub username.
- `DOCKERHUB_TOKEN`: Your DockerHub personal access token.

## Running with Docker

### Option 1: DockerHub Image (Recommended)
This is the easiest way to run the application as it does not require a local Java/Maven setup. 
Ensure you have a PostgreSQL instance running (you can use the `db` service from our `docker-compose.yml`).

```bash
docker run -p 8080:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/undoschool_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  mahangarerohan1203/class-booking-system:latest
```

### Option 2: Docker Compose
Builds and starts both the application and the database from source.
```bash
# Navigate to the project directory
cd class-booking-system

# Build and start services
docker-compose up --build -d
```

## Dockerization
A `Dockerfile` is provided for containerizing the application. 
The official image is available on DockerHub: `mahangarerohan1203/class-booking-system`
