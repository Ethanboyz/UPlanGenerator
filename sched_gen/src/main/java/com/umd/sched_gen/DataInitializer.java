package com.umd.sched_gen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.umd.sched_gen.Courses.Course;
import com.umd.sched_gen.Courses.CourseRepository;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

/* Exists solely to populate and update the database, when needed */
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
        if (courseRepository.count() == 0) {
            List<Course> courses = apiService.fetchAllCourses();
            saveCourses(courses);
        }
    }
    // Save courses one at a time to database, ensuring none are duplicates
    private void saveCourses(List<Course> courses) {
        for (Course course : courses) {
            try {
                courseRepository.save(course);
            } catch (DataIntegrityViolationException d) {
                System.out.println("Course not added: " + course.getCourseId() + ", possible duplicate?");
            } catch (Exception e) {
                System.out.println("Failed to save course " + course.getCourseId() + " to database");
            }
        }
    }
}
