package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.OfferingDto;
import com.undoschool.class_booking_system.dto.SessionDto;
import com.undoschool.class_booking_system.entity.Course;
import com.undoschool.class_booking_system.entity.Offering;
import com.undoschool.class_booking_system.entity.Session;
import com.undoschool.class_booking_system.entity.Teacher;
import com.undoschool.class_booking_system.exception.ResourceNotFoundException;
import com.undoschool.class_booking_system.repository.CourseRepository;
import com.undoschool.class_booking_system.repository.OfferingRepository;
import com.undoschool.class_booking_system.repository.SessionRepository;
import com.undoschool.class_booking_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final OfferingRepository offeringRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public OfferingDto createOffering(Long teacherId, OfferingDto offeringDto) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        
        Course course = courseRepository.findById(offeringDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Offering offering = Offering.builder()
                .teacher(teacher)
                .course(course)
                .title(offeringDto.getTitle())
                .capacity(offeringDto.getCapacity())
                .build();

        Offering saved = offeringRepository.save(offering);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public OfferingDto addSessions(Long teacherId, Long offeringId, List<SessionDto> sessionDtos) {
        if (sessionDtos == null || sessionDtos.isEmpty()) {
            throw new IllegalArgumentException("Session list cannot be empty");
        }

        // Validate that new sessions don't overlap with each other
        validateInternalOverlaps(sessionDtos);

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (!offering.getTeacher().getTeacherId().equals(teacherId)) {
            throw new IllegalStateException("Teacher does not own this offering");
        }

        // Fetch all existing sessions for this teacher to check for conflicts
        List<Session> existingSessions = sessionRepository.findAllByTeacherId(teacherId);

        for (SessionDto newDto : sessionDtos) {
            // Conflict Detection: Teacher cannot have overlapping sessions across any offerings
            for (Session existing : existingSessions) {
                if (isOverlappingWithEntity(newDto, existing)) {
                    throw new IllegalStateException(String.format(
                        "Schedule conflict: Teacher already has a session from %s to %s",
                        existing.getStartTime(), existing.getEndTime()
                    ));
                }
            }

            Session session = Session.builder()
                    .startTime(newDto.getStartTime())
                    .endTime(newDto.getEndTime())
                    .offering(offering) // Ensure relationship is set
                    .build();
            offering.addSession(session);
        }

        Offering saved = offeringRepository.save(offering);
        return mapToDto(saved);
    }

    private void validateInternalOverlaps(List<SessionDto> sessionDtos) {
        for (int i = 0; i < sessionDtos.size(); i++) {
            for (int j = i + 1; j < sessionDtos.size(); j++) {
                if (isOverlappingDtos(sessionDtos.get(i), sessionDtos.get(j))) {
                    throw new IllegalArgumentException("Provided sessions have internal overlaps");
                }
            }
        }
    }

    private boolean isOverlappingDtos(SessionDto s1, SessionDto s2) {
        return s1.getStartTime().isBefore(s2.getEndTime()) && 
               s1.getEndTime().isAfter(s2.getStartTime());
    }

    private boolean isOverlappingWithEntity(SessionDto s1, Session s2) {
        return s1.getStartTime().isBefore(s2.getEndTime()) && 
               s1.getEndTime().isAfter(s2.getStartTime());
    }

    @Override
    public List<OfferingDto> getTeacherOfferings(Long teacherId) {
        return offeringRepository.findAllByTeacherIdWithSessions(teacherId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private OfferingDto mapToDto(Offering offering) {
        return OfferingDto.builder()
                .offeringId(offering.getOfferingId())
                .courseId(offering.getCourse().getCourseId())
                .teacherId(offering.getTeacher().getTeacherId())
                .title(offering.getTitle())
                .capacity(offering.getCapacity())
                .status(offering.getStatus())
                .createdAt(offering.getCreatedAt())
                .sessions(offering.getSessions().stream()
                        .map(this::mapToSessionDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private SessionDto mapToSessionDto(Session session) {
        return SessionDto.builder()
                .sessionId(session.getSessionId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }
}
