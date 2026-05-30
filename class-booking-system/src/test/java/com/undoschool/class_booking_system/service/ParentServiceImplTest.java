package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.BookingDto;
import com.undoschool.class_booking_system.entity.*;
import com.undoschool.class_booking_system.repository.BookingRepository;
import com.undoschool.class_booking_system.repository.OfferingRepository;
import com.undoschool.class_booking_system.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceImplTest {

    @Mock
    private OfferingRepository offeringRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ParentServiceImpl parentService;

    private Student student;
    private Offering offering;
    private Session session;

    @BeforeEach
    void setUp() {
        student = Student.builder().studentId(1L).name("Student").email("s@s.com").build();
        
        Course course = Course.builder().courseId(1L).title("Course").build();
        
        offering = Offering.builder()
                .offeringId(1L)
                .course(course)
                .teacher(Teacher.builder().teacherId(1L).build())
                .title("Offering")
                .capacity(10)
                .status(OfferingStatus.ACTIVE)
                .sessions(new ArrayList<>())
                .build();
        
        session = Session.builder()
                .sessionId(1L)
                .offering(offering)
                .startTime(OffsetDateTime.parse("2026-06-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2026-06-01T11:00:00Z"))
                .build();
        
        offering.getSessions().add(session);
    }

    @Test
    void bookOffering_ShouldSucceed_WhenNoConflicts() {
        BookingDto dto = BookingDto.builder().offeringId(1L).idempotencyKey("key1").build();
        
        when(bookingRepository.findByIdempotencyKey("key1")).thenReturn(Optional.empty());
        when(offeringRepository.findByIdWithLock(1L)).thenReturn(Optional.of(offering));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BookingDto result = parentService.bookOffering(1L, dto);

        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void bookOffering_ShouldDetectConflict_WhenSessionsOverlap() {
        BookingDto dto = BookingDto.builder().offeringId(1L).idempotencyKey("key2").build();
        
        // Mock existing booking with overlapping session
        Booking existingBooking = Booking.builder()
                .bookingId(10L)
                .student(student)
                .status(BookingStatus.CONFIRMED)
                .offering(Offering.builder()
                        .offeringId(2L) // Set a different offering ID
                        .title("Existing")
                        .sessions(List.of(Session.builder()
                                .startTime(OffsetDateTime.parse("2026-06-01T10:30:00Z")) // Overlaps
                                .endTime(OffsetDateTime.parse("2026-06-01T11:30:00Z"))
                                .build()))
                        .build())
                .build();

        when(bookingRepository.findByIdempotencyKey("key2")).thenReturn(Optional.empty());
        when(offeringRepository.findByIdWithLock(1L)).thenReturn(Optional.of(offering));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(bookingRepository.findConfirmedBookingsWithSessionsByStudentId(1L)).thenReturn(List.of(existingBooking));

        assertThrows(IllegalStateException.class, () -> parentService.bookOffering(1L, dto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookOffering_ShouldWaitlist_WhenCapacityFull() {
        BookingDto dto = BookingDto.builder().offeringId(1L).idempotencyKey("key3").build();
        offering.setCapacity(1);

        when(bookingRepository.findByIdempotencyKey("key3")).thenReturn(Optional.empty());
        when(offeringRepository.findByIdWithLock(1L)).thenReturn(Optional.of(offering));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(bookingRepository.countByOfferingOfferingIdAndStatus(1L, BookingStatus.CONFIRMED)).thenReturn(1L);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BookingDto result = parentService.bookOffering(1L, dto);

        assertEquals(BookingStatus.WAITLISTED, result.getStatus());
    }
}
