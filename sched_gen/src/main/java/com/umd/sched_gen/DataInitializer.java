package com.umd.sched_gen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.umd.sched_gen.Courses.Course;
import com.umd.sched_gen.Courses.CourseRepository;

import org.springframework.dao.DataIntegrityViolationException;

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

        // Save courses one at a time to database, ensure none are duplicates
        for (Course course : courses) {
            try {
                courseRepository.save(course);
            } catch (DataIntegrityViolationException d) {
                System.out.println("Duplicate course not added: " + course.getCourseId());
            } catch (Exception e) {
                System.out.println("Failed to save course " + course.getCourseId() + " to database");
            }
        }
    }
}
