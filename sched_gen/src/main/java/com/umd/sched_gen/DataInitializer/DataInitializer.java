package com.umd.sched_gen.DataInitializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.umd.sched_gen.Courses.Course;
import com.umd.sched_gen.Courses.CourseRepository;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

/** A component that manages the initialization of Course data in the database */
@Component
public class DataInitializer implements CommandLineRunner {
    private final ApiService apiService;
    private final CourseRepository courseRepository;
    private final ApplicationEventPublisher publisher;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    /** Constructor defines explicit dependencies for this component to run
     * 
     * @param apiService the service that will retrieve Course data
     * @param courseRepository handler of CRUD operations for the database
     * @param publisher the publisher for the event indicating initializer of the database is done
    */
    public DataInitializer(ApiService apiService, CourseRepository courseRepository, ApplicationEventPublisher publisher) {
        this.apiService = apiService;
        this.courseRepository = courseRepository;
        this.publisher = publisher;
    }

    /** Initializes the database course table with UMD Courses and publishes a DataInitializedEvent
     * when done
    */
    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            List<Course> courses = apiService.fetchAllCourses();
            saveCourses(courses);
        }
        publisher.publishEvent(new DataInitializedEvent(this));
    }
    
    /** Save a list of Courses to the courses table of the database
     * 
     * @param courses the Courses to be saved to database
     */
    private void saveCourses(List<Course> courses) {
        for (Course course : courses) {
            try {
                courseRepository.save(course);
            } catch (DataIntegrityViolationException d) {
                System.out.println(ANSI_RED + "Course not added: " + course.getCourseId()
                                    + ANSI_RESET);
            } catch (Exception e) {
                System.out.println(ANSI_RED + "Failed to save course " + course.getCourseId() + 
                                    " to database" + ANSI_RESET);
            }
        }
    }
}
