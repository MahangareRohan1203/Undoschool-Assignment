package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.CourseDto;
import com.undoschool.class_booking_system.dto.StudentDto;
import com.undoschool.class_booking_system.dto.TeacherDto;

import java.util.List;

public interface ManagementService {
    CourseDto createCourse(CourseDto courseDto);
    List<CourseDto> getAllCourses();
    TeacherDto createTeacher(TeacherDto teacherDto);
    StudentDto createStudent(StudentDto studentDto);
}
