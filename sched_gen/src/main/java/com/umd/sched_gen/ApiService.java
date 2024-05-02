package com.umd.sched_gen;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.umd.sched_gen.Courses.Course;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Year;

@Service
public class ApiService {
    private final String COURSES_API = "https://api.umd.io/v1/courses";
    private final int COURSES_PER_PAGE = 100;
    private final String GRADES_API = "https://planetterp.com/api/v1/course";
    private final int RETRIEVAL_RATE = 5;       /* Up to 1000 */
    private final RestTemplate restTemplate;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public ApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /* Fetch all courses from umd.io, returning a list of all courses.
     * Additionally fetches grades from planetterp.com. Use to populate the database. */
    public List<Course> fetchAllCourses() {
        List<Course> allCourses = new ArrayList<>();
        int page = 1;

        do {    /* umd.io returns paginated list of courses, must traverse through pages */
            /* Build the request for umd.io */
            String courseUrl = UriComponentsBuilder.fromHttpUrl(COURSES_API)
                .queryParam("page", page++)
                .queryParam("per_page", COURSES_PER_PAGE)
                .toUriString();
            /* Fetch course data from API(s) */
            try {
                ResponseEntity<List<Course>> response = restTemplate.exchange(
                courseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Course>>() {});
                
                /* Process the response and add to list. Do this one page at a time */
                List<Course> fetchedCourses = response.getBody();
                if (fetchedCourses != null && !fetchedCourses.isEmpty()) {
                    fetchedCourses = refineCourses(fetchedCourses);
                    allCourses.addAll(fetchedCourses);
                } else {
                    break;   // Break the loop when there are no more courses
                }
            } catch (RestClientException e) {
                System.out.println("API GET request failed! " + courseUrl);
            }

            // No API rate limits, but slow down anyway because we're nice :3
            try {
                Thread.sleep(Math.max((1000 / RETRIEVAL_RATE), 1));
            } catch (InterruptedException t) {
                System.out.println("Interrupted thread");
            }
        } while (true);

        return allCourses;  /* Should contain all courses and all their data */
    }

    /* Processing courses after fetching from umd.io */
    private List<Course> refineCourses(List<Course> courses) {
        ArrayList<Course> toBeRemoved = new ArrayList<>();
        for (Course course : courses) {
            /* We want to filter grad-level courses from the database (maybe will change in the future) */
            Pattern pattern = Pattern.compile("[A-Z]{4}[0-4]\\d{2}[0-9A-Z]?");
            Matcher matcher = pattern.matcher(course.getCourseId());
            if (matcher.find()) {
                /* Fetch average GPA for each remaining course and semesters taught data */
                course.setAverageGPA(fetchGradesData(course));
                ArrayList<String> semestersTaught = fetchSemesterData(course);
                course.setSemesters(semestersTaught);
                course.setNumSemesters(semestersTaught.size());
                System.out.println(ANSI_CYAN + "[DEBUG]: " + course.getCourseId() + " has relationships: \n"
                                + "\t" + course.getRelationships().toString() + ANSI_RESET);
            } else {
                toBeRemoved.add(course);
                System.out.println(ANSI_YELLOW + "[NOTICE]: " + course.getCourseId()
                                    + " is not undergrad level! Removing..." + ANSI_RESET);
            }
        }
        courses.removeAll(toBeRemoved);
        return courses;
    }

    /* Retrieves grades data from planetterp.com for a course. Used in refineCourses() */
    private float fetchGradesData(Course course) {
        String courseUrl = UriComponentsBuilder.fromHttpUrl(GRADES_API)
            .queryParam("name", course.getCourseId())
            .toUriString();
        try {
            ResponseEntity<Course> response = restTemplate.exchange(
            courseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Course>() {});

            /* Process the response and return the grade */
            Course fetchedCourse = response.getBody();
            return fetchedCourse == null? 0.0F:fetchedCourse.getAverageGPA();
        } catch (RestClientException e) {
            System.out.println(ANSI_GREEN + "[GRADES]: No grade data for " + course.getCourseId() + ","
                                + " defaulting to 0.0" + ANSI_RESET);
            return 0.0F;
        }
    }

    /* Retrieves semester data from umd.io for a course. Used in refineCourses() */
    private ArrayList<String> fetchSemesterData(Course course) {
        ArrayList<String> fetchedSemesters = new ArrayList<>();
        String year = String.valueOf(Year.now().getValue());
        HashMap<String, String> possibleSemesters = new HashMap<>();
        
        /* Each semester is associated with a year and a two digit number representing start month */
        possibleSemesters.put(year + "08", "FALL");
        possibleSemesters.put(year + "12", "WINTER");
        possibleSemesters.put(year + "01", "SPRING");
        possibleSemesters.put(year + "05", "SUMMER");
        String courseId = course.getCourseId();
        
        possibleSemesters.forEach((possibleSemester, semesterWordForm) -> {
            String courseUrl = UriComponentsBuilder.fromHttpUrl(COURSES_API)
                .queryParam("name", courseId)
                .queryParam("semester", possibleSemester)
                .toUriString();
            try {
                restTemplate.exchange(
                courseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Course>() {});
                fetchedSemesters.add(semesterWordForm);
                System.out.println(ANSI_GREEN + "[SEMESTERS]: Class " + course.getCourseId() + " is taught during"
                                + semesterWordForm + ANSI_RESET);
            } catch (RestClientException e) {
                System.out.println(ANSI_GREEN + "[SEMESTERS]: Class " + course.getCourseId() + " not offered during"
                                + semesterWordForm + ANSI_RESET);
            }
        });
        return fetchedSemesters;
    }
}
