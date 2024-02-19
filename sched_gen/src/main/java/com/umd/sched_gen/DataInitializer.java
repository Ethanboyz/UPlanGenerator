package com.umd.sched_gen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ApiService apiService;
    private final CourseRepository courseRepository;

    @Autowired
    public DataInitializer(ApiService apiService, CourseRepository courseRepository) {
        this.apiService = apiService;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        List<Course> courses = apiService.fetchCourses();
        courseRepository.saveAll(courses);
    }
}
