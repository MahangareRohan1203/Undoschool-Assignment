package com.undoschool.class_booking_system.controller;

import com.undoschool.class_booking_system.dto.OfferingDto;
import com.undoschool.class_booking_system.dto.SessionDto;
import com.undoschool.class_booking_system.service.TeacherService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Validated
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/offerings")
    public ResponseEntity<OfferingDto> createOffering(
            @Parameter(description = "Teacher ID from header") @RequestHeader("X-Teacher-Id") @Positive Long teacherId,
            @Valid @RequestBody OfferingDto offeringDto) {
        return new ResponseEntity<>(teacherService.createOffering(teacherId, offeringDto), HttpStatus.CREATED);
    }

    @PostMapping("/offerings/{id}/sessions")
    public ResponseEntity<OfferingDto> addSessions(
            @Parameter(description = "Teacher ID from header") @RequestHeader("X-Teacher-Id") @Positive Long teacherId,
            @PathVariable @Positive Long id,
            @Valid @RequestBody List<SessionDto> sessionDtos) {
        return ResponseEntity.ok(teacherService.addSessions(teacherId, id, sessionDtos));
    }

    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingDto>> getTeacherOfferings(
            @Parameter(description = "Teacher ID from header") @RequestHeader("X-Teacher-Id") @Positive Long teacherId) {
        return ResponseEntity.ok(teacherService.getTeacherOfferings(teacherId));
    }
}
