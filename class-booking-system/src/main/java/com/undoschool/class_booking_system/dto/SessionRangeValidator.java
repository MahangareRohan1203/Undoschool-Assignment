package com.undoschool.class_booking_system.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SessionRangeValidator implements ConstraintValidator<ValidSessionRange, SessionDto> {

    @Override
    public boolean isValid(SessionDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            return true;
        }
        return dto.getStartTime().isBefore(dto.getEndTime());
    }
}
