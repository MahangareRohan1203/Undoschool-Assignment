# Class Booking System

A robust, scalable Spring Boot application for managing class bookings, offerings, and sessions. This system allows teachers to create offerings, parents to book classes for their students, and management to oversee the entire process.

## Project Overview

The Class Booking System is designed to handle the complexities of scheduling and booking educational sessions. Key features include:
- **Offering Management:** Teachers can create and manage course offerings with specific capacities.
- **Session Scheduling:** Flexible session timing within offerings.
- **Booking & Waitlisting:** Automated booking process with support for waitlisting when capacity is reached.
- **Conflict Detection:** Prevents students from booking overlapping sessions.
- **Idempotency:** Ensures reliable booking even in case of network retries.

## Tech Stack Used

- **Java 17:** Core programming language.
- **Spring Boot 3.3.5:** Application framework.
- **Spring Data JPA:** For database ORM.
- **PostgreSQL:** Relational database for persistent storage.
- **Liquibase:** Database schema migration and version control.
- **SpringDoc OpenAPI (Swagger):** For interactive API documentation.
- **Lombok:** To reduce boilerplate code.
- **Maven:** Build and dependency management.
- **Docker:** Containerization for consistent development and deployment environments.

## Setup Instructions

### Prerequisites
- JDK 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL (if running locally without Docker)

### Environment Variables Required

The application can be configured using the following environment variables (defaults provided in `application.yaml`):

| Variable      | Description                      | Default Value                                |
|---------------|----------------------------------|----------------------------------------------|
| `DB_URL`      | PostgreSQL JDBC URL              | `jdbc:postgresql://localhost:5432/undoschool_db` |
| `DB_USERNAME` | Database username                | `postgres`                                   |
| `DB_PASSWORD` | Database password                | `postgres`                                   |
| `SERVER_PORT` | Port on which the server runs    | `8080`                                       |

### Steps to Run the Application Locally

#### Using Docker (Recommended)
The easiest way to get started is using Docker Compose, which sets up both the application and the PostgreSQL database.

1. Clone the repository.
2. Build the project:
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. Start the containers:
   ```bash
   docker-compose up --build
   ```
4. The application will be accessible at `http://localhost:8080`.

#### Running Manually
1. Ensure you have a PostgreSQL database running and create a database named `undoschool_db`.
2. Update the environment variables or the `application.yaml` file with your database credentials.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Documentation

The application uses OpenAPI/Swagger for documentation. Once the application is running, you can access:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs (JSON):** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

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
- **`courses`**: Stores base course information (title, description).
- **`teachers`**: Teacher details.
- **`students`**: Student details.
- **`offerings`**: Specific instances of a course taught by a teacher, with capacity limits.
- **`sessions`**: Time slots associated with an offering.
- **`bookings`**: Records of student bookings for offerings, including their status (CONFIRMED/WAITLISTED).

Schema migrations are managed by **Liquibase**, located in `src/main/resources/db/changelog`.

## Concurrency Handling Approach

To ensure data integrity and prevent overbooking, the system employs several strategies:

1. **Pessimistic Locking:** When a booking request is received, the application uses a `PESSIMISTIC_WRITE` lock on the specific `Offering` record. This ensures that only one booking process can modify/check the capacity of an offering at a time, preventing race conditions.
2. **Idempotency:** Clients must provide a unique `idempotency_key` for each booking request. If a request with the same key is received again (e.g., due to a network retry), the system returns the original result instead of creating a duplicate booking.
3. **Waitlisting:** When an offering reaches its capacity, subsequent bookings are automatically assigned a `WAITLISTED` status instead of being rejected, providing a better user experience.
4. **Transactional Integrity:** All booking operations are wrapped in `@Transactional` to ensure atomicity—either the entire booking succeeds (including conflict checks and status updates) or nothing is persisted.

## Timezone Handling Approach

The system is designed to be globally compatible by handling timezones explicitly:

- **Database Storage:** All timestamp columns (`created_at`, `start_time`, `end_time`) use the `TIMESTAMPTZ` (timestamp with time zone) type in PostgreSQL. This stores the absolute point in time.
- **Application Logic:** The Java application uses `OffsetDateTime` for all temporal fields. This preserves the offset information from the client and ensures consistent calculations regardless of the server's local time.
- **API Communication:** Timestamps are exchanged in ISO-8601 format (e.g., `2026-05-30T10:00:00+05:30`), ensuring clear communication between the frontend and backend.

## Assumptions Made

1. **Authentication:** For the scope of this project, authentication is simplified using custom headers (`X-Student-Id` and `X-Teacher-Id`). In a production environment, this would be replaced by JWT or OAuth2.
2. **One Student per Parent:** The current implementation assumes a one-to-one mapping between the "Parent" user role and the "Student" entity for simplicity.
3. **Session Consistency:** Once an offering is active and has bookings, sessions are assumed to be fixed. Modifying sessions of a confirmed booking requires manual intervention or a more complex cancellation/re-booking flow not included in this MVP.
4. **Currency:** All financial transactions (if any were to be added) are assumed to be handled outside this system or in a future module.
