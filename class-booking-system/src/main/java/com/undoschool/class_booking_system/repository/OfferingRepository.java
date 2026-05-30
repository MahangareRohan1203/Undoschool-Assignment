package com.undoschool.class_booking_system.repository;

import com.undoschool.class_booking_system.entity.Offering;
import com.undoschool.class_booking_system.entity.OfferingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offering o WHERE o.offeringId = :id")
    Optional<Offering> findByIdWithLock(Long id);

    @Query("SELECT DISTINCT o FROM Offering o JOIN FETCH o.sessions JOIN FETCH o.course JOIN FETCH o.teacher WHERE o.teacher.teacherId = :teacherId")
    List<Offering> findAllByTeacherIdWithSessions(Long teacherId);

    @Query("SELECT DISTINCT o FROM Offering o JOIN FETCH o.sessions JOIN FETCH o.course JOIN FETCH o.teacher WHERE o.status = :status")
    List<Offering> findAllByStatusWithSessions(OfferingStatus status);
}
