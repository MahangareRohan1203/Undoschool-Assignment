package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.BookingDto;
import com.undoschool.class_booking_system.dto.OfferingDto;

import java.util.List;

public interface ParentService {
    List<OfferingDto> getAvailableOfferings();
    BookingDto bookOffering(Long studentId, BookingDto bookingDto);
    List<BookingDto> getStudentBookings(Long studentId);
}
