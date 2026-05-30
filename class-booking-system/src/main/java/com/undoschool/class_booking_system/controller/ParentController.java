package com.undoschool.class_booking_system.controller;

import com.undoschool.class_booking_system.dto.BookingDto;
import com.undoschool.class_booking_system.dto.OfferingDto;
import com.undoschool.class_booking_system.service.ParentService;
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
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Validated
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingDto>> getAvailableOfferings() {
        return ResponseEntity.ok(parentService.getAvailableOfferings());
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingDto> bookOffering(
            @Parameter(description = "Student ID from header") @RequestHeader("X-Student-Id") @Positive Long studentId,
            @Valid @RequestBody BookingDto bookingDto) {
        return new ResponseEntity<>(parentService.bookOffering(studentId, bookingDto), HttpStatus.CREATED);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDto>> getStudentBookings(
            @Parameter(description = "Student ID from header") @RequestHeader("X-Student-Id") @Positive Long studentId) {
        return ResponseEntity.ok(parentService.getStudentBookings(studentId));
    }
}
