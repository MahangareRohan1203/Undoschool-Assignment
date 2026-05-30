package com.undoschool.class_booking_system.controller;

import com.undoschool.class_booking_system.dto.CourseDto;
import com.undoschool.class_booking_system.dto.StudentDto;
import com.undoschool.class_booking_system.dto.TeacherDto;
import com.undoschool.class_booking_system.service.ManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    @PostMapping("/courses")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        return new ResponseEntity<>(managementService.createCourse(courseDto), HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(managementService.getAllCourses());
    }

    @PostMapping("/teachers")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        return new ResponseEntity<>(managementService.createTeacher(teacherDto), HttpStatus.CREATED);
    }

    @PostMapping("/parents")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        return new ResponseEntity<>(managementService.createStudent(studentDto), HttpStatus.CREATED);
    }
}
