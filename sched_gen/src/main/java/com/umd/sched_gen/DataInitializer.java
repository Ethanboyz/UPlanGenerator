package com.umd.sched_gen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.umd.sched_gen.Courses.Course;
import com.umd.sched_gen.Courses.CourseRepository;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ApiService apiService;
    private final CourseRepository courseRepository;

    public DataInitializer(ApiService apiService, CourseRepository courseRepository) {
        this.apiService = apiService;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        List<Course> courses = apiService.fetchAllCourses();
        courseRepository.saveAll(courses);
    }
}
