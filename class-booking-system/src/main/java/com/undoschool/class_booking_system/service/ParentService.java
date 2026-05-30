package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.BookingDto;
import com.undoschool.class_booking_system.dto.OfferingDto;
import com.undoschool.class_booking_system.dto.SessionDto;
import com.undoschool.class_booking_system.entity.*;
import com.undoschool.class_booking_system.exception.ResourceNotFoundException;
import com.undoschool.class_booking_system.repository.BookingRepository;
import com.undoschool.class_booking_system.repository.OfferingRepository;
import com.undoschool.class_booking_system.repository.SessionRepository;
import com.undoschool.class_booking_system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final OfferingRepository offeringRepository;
    private final BookingRepository bookingRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;

    public List<OfferingDto> getAvailableOfferings() {
        return offeringRepository.findAllByStatusWithSessions(OfferingStatus.ACTIVE).stream()
                .map(this::mapToOfferingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto bookOffering(Long studentId, BookingDto bookingDto) {
        // Handle idempotent retries
        Optional<Booking> existing = bookingRepository.findByIdempotencyKey(bookingDto.getIdempotencyKey());
        if (existing.isPresent()) {
            return mapToBookingDto(existing.get());
        }

        // Lock offering to prevent race conditions on capacity checks
        Offering offering = offeringRepository.findByIdWithLock(bookingDto.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (offering.getStatus() != OfferingStatus.ACTIVE) {
            throw new IllegalStateException("Offering is not active");
        }

        // Prevent multiple simultaneous bookings for the same offering
        boolean alreadyBooked = bookingRepository.existsByStudentStudentIdAndOfferingOfferingIdAndStatusIn(
                studentId, offering.getOfferingId(), List.of(BookingStatus.CONFIRMED, BookingStatus.WAITLISTED));
        if (alreadyBooked) {
            throw new IllegalStateException("Student already has a booking (Confirmed or Waitlisted) for this offering");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Determine booking status based on current availability
        long currentBookingsCount = bookingRepository.countByOfferingOfferingIdAndStatus(offering.getOfferingId(), BookingStatus.CONFIRMED);

        BookingStatus bookingStatus = BookingStatus.CONFIRMED;
        if (currentBookingsCount >= offering.getCapacity()) {
            bookingStatus = BookingStatus.WAITLISTED;
        }

        // Check for session overlaps only if the booking is confirmed
        if (bookingStatus == BookingStatus.CONFIRMED) {
            checkForConflicts(studentId, offering);
        }

        Booking booking = Booking.builder()
                .student(student)
                .offering(offering)
                .status(bookingStatus)
                .idempotencyKey(bookingDto.getIdempotencyKey())
                .build();

        Booking saved = bookingRepository.save(booking);
        return mapToBookingDto(saved);
    }

    private void checkForConflicts(Long studentId, Offering newOffering) {
        List<Booking> studentBookings = bookingRepository.findConfirmedBookingsWithSessionsByStudentId(studentId);

        for (Booking existingBooking : studentBookings) {
            for (Session existingSession : existingBooking.getOffering().getSessions()) {
                for (Session newSession : newOffering.getSessions()) {
                    if (isOverlapping(existingSession, newSession)) {
                        throw new IllegalStateException(String.format(
                            "Conflict detected: Session from '%s' overlaps with existing booking '%s'",
                            newOffering.getTitle(), existingBooking.getOffering().getTitle()
                        ));
                    }
                }
            }
        }
    }

    private boolean isOverlapping(Session s1, Session s2) {
        // Standard interval overlap check: (StartA < EndB) and (EndA > StartB)
        return s1.getStartTime().isBefore(s2.getEndTime()) && 
               s1.getEndTime().isAfter(s2.getStartTime());
    }

    public List<BookingDto> getStudentBookings(Long studentId) {
        return bookingRepository.findAllByStudentIdWithSessions(studentId).stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private OfferingDto mapToOfferingDto(Offering offering) {
        return OfferingDto.builder()
                .offeringId(offering.getOfferingId())
                .courseId(offering.getCourse().getCourseId())
                .teacherId(offering.getTeacher().getTeacherId())
                .title(offering.getTitle())
                .capacity(offering.getCapacity())
                .status(offering.getStatus())
                .createdAt(offering.getCreatedAt())
                .sessions(offering.getSessions().stream()
                        .map(s -> SessionDto.builder()
                                .sessionId(s.getSessionId())
                                .startTime(s.getStartTime())
                                .endTime(s.getEndTime())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .offeringId(booking.getOffering().getOfferingId())
                .studentId(booking.getStudent().getStudentId())
                .idempotencyKey(booking.getIdempotencyKey())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
