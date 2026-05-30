package com.undoschool.class_booking_system.repository;

import com.undoschool.class_booking_system.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    @Query("SELECT s FROM Session s JOIN FETCH s.offering o JOIN FETCH o.teacher t WHERE t.teacherId = :teacherId")
    List<Session> findAllByTeacherId(Long teacherId);
}
