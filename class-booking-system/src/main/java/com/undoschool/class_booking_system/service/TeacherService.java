package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.OfferingDto;
import com.undoschool.class_booking_system.dto.SessionDto;

import java.util.List;

public interface TeacherService {
    OfferingDto createOffering(Long teacherId, OfferingDto offeringDto);
    OfferingDto addSessions(Long teacherId, Long offeringId, List<SessionDto> sessionDtos);
    List<OfferingDto> getTeacherOfferings(Long teacherId);
}
