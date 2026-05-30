package com.undoschool.class_booking_system.service;

import com.undoschool.class_booking_system.dto.CourseDto;
import com.undoschool.class_booking_system.dto.StudentDto;
import com.undoschool.class_booking_system.dto.TeacherDto;
import com.undoschool.class_booking_system.entity.Course;
import com.undoschool.class_booking_system.entity.Student;
import com.undoschool.class_booking_system.entity.Teacher;
import com.undoschool.class_booking_system.repository.CourseRepository;
import com.undoschool.class_booking_system.repository.StudentRepository;
import com.undoschool.class_booking_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementServiceImpl implements ManagementService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = Course.builder()
                .title(courseDto.getTitle())
                .description(courseDto.getDescription())
                .build();
        Course saved = courseRepository.save(course);
        return mapToDto(saved);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeacherDto createTeacher(TeacherDto teacherDto) {
        Teacher teacher = Teacher.builder()
                .name(teacherDto.getName())
                .email(teacherDto.getEmail())
                .build();
        Teacher saved = teacherRepository.save(teacher);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = Student.builder()
                .name(studentDto.getName())
                .email(studentDto.getEmail())
                .build();
        Student saved = studentRepository.save(student);
        return mapToDto(saved);
    }

    private CourseDto mapToDto(Course course) {
        return CourseDto.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .build();
    }

    private TeacherDto mapToDto(Teacher teacher) {
        return TeacherDto.builder()
                .teacherId(teacher.getTeacherId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .build();
    }

    private StudentDto mapToDto(Student student) {
        return StudentDto.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .email(student.getEmail())
                .build();
    }
}
