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

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (!offering.getTeacher().getTeacherId().equals(teacherId)) {
            throw new IllegalStateException("Teacher does not own this offering");
        }

        for (SessionDto dto : sessionDtos) {
            if (dto.getStartTime().isAfter(dto.getEndTime())) {
                throw new IllegalArgumentException("Session start time must be before end time");
            }
            Session session = Session.builder()
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .build();
            offering.addSession(session);
        }

        Offering saved = offeringRepository.save(offering);
        return mapToDto(saved);
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
