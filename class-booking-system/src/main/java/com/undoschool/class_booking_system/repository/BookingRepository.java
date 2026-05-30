package com.undoschool.class_booking_system.repository;

import com.undoschool.class_booking_system.entity.Booking;
import com.undoschool.class_booking_system.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    long countByOfferingOfferingIdAndStatus(Long offeringId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.offering o JOIN FETCH o.sessions WHERE b.student.studentId = :studentId AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsWithSessionsByStudentId(Long studentId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.offering o JOIN FETCH o.sessions WHERE b.student.studentId = :studentId")
    List<Booking> findAllByStudentIdWithSessions(Long studentId);

    boolean existsByStudentStudentIdAndOfferingOfferingIdAndStatusIn(Long studentId, Long offeringId, List<BookingStatus> statuses);
}
