# Global Class Offering Booking System - Planner

## 1. Requirement Gathering

### Functional Requirements
- [x] **Teachers**:
  - Create offerings.
  - Add multiple sessions to an offering (Start/End times, Timezone awareness).
  - List their offerings and sessions.
- [x] **Parents**:
  - Browse available offerings (converted to their local timezone).
  - Book an entire offering (all sessions).
  - View their bookings.
- [x] **Booking Rules**:
  - Atomicity: Bookings are for the whole offering.
  - Conflict Detection: Prevent booking an offering if any of its sessions overlap with already booked sessions for that parent.
  - Concurrency: Handle simultaneous booking attempts for the same offering or by the same parent.

### Non-Functional Requirements
- [x] **Timezone Management**: All times stored in UTC. Input/Output handled with timezone offsets using `OffsetDateTime`.
- [x] **Data Integrity**: Ensure no overlapping bookings for a single parent.
- [x] **Scalability**: Clean separation of concerns (Controller-Service-Repository).

### 10% Scope Extension
1. [x] **Offering Capacity & Waitlist**: Each offering has a maximum capacity. If full, parents can join a waitlist.
2. [x] **Booking Idempotency**: Use idempotency keys for booking requests.
3. [ ] **Soft Delete**: Support decommissioning offerings (Optional/Next Step).

---

## 2. Core Entities

- [x] **Teacher** (`teacher_id`, `name`, `email`)
- [x] **Student/Parent** (`student_id`, `name`, `email`)
- [x] **Course** (`course_id`, `title`, `description`)
- [x] **Offering** (`offering_id`, `course_id`, `teacher_id`, `title`, `capacity`, `status`)
- [x] **Session** (`session_id`, `offering_id`, `start_time`, `end_time`)
- [x] **Booking** (`booking_id`, `offering_id`, `student_id`, `status`, `idempotency_key`)

---

## 3. API Endpoints

### [x] Management APIs (For Setup/Testing)
- `POST /api/v1/courses`: Create a course.
- `POST /api/v1/teachers`: Register a teacher.
- `POST /api/v1/parents`: Register a student/parent.

### [x] Teacher APIs (`/api/v1/teachers`)
- `POST /offerings`: Create a new offering (requires `course_id` and `X-Teacher-Id` header).
- `POST /offerings/{id}/sessions`: Add sessions to an offering (requires `X-Teacher-Id` header).
- `GET /offerings`: List teacher's offerings with session details (requires `X-Teacher-Id` header).

### [x] Parent APIs (`/api/v1/parents`)
- `GET /offerings`: List all available offerings.
- `POST /bookings`: Book an offering (requires `offering_id` and `X-Student-Id` header).
- `GET /bookings`: List parent's bookings (requires `X-Student-Id` header).

---

## 4. High Level Design

- **Framework**: Java 17 + Spring Boot 3
- **Database**: PostgreSQL
- **Persistence**: Spring Data JPA + Hibernate
- **Migration**: Liquibase
- **Identity Management**: Simplified header-based identification (`X-Teacher-Id`, `X-Student-Id`).
- **Concurrency Control**:
  - Database Transactions.
  - Conflict detection algorithm in `ParentService`.
  - Idempotency handling in `ParentService`.

---

## 5. Implementation Steps

1. [x] Project Initialization (Maven, Spring Boot 3.2).
2. [x] Database Schema Setup (Entities & Liquibase migrations).
3. [x] Teacher Service & Controllers.
4. [x] Booking Service & Conflict Detection Logic.
5. [x] Concurrency & Idempotency implementation.
6. [x] Unit & Integration Tests.
7. [x] Final Documentation and README completion.
