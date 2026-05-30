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
- **Framework**: Spring Boot 3.2+
- **Database**: PostgreSQL
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

## Assignment Requirements Coverage
- [x] Clean backend design
- [x] Correct handling of concurrency
- [x] Good database design
- [x] Proper timezone handling
- [x] Thoughtful error handling
- [x] Conflict detection logic
- [x] 10% Scope Extension: Waitlist and Idempotency
