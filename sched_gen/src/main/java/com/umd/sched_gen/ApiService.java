package com.umd.sched_gen;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
            String courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API)
                .queryParam("page", page++)
                .queryParam("per_page", COURSES_PER_PAGE)
                .toUriString();
            /* Fetch course data from API(s) */
            try {
                ResponseEntity<List<Course>> response = restTemplate.exchange(
                courseUri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Course>>() {});
                
                /* Process the response and add to list. Do this one page at a time */
                List<Course> fetchedCourses = response.getBody();
                if (fetchedCourses != null && !fetchedCourses.isEmpty()) {
                    fetchedCourses = refineCourses(fetchedCourses);
                    allCourses.addAll(fetchedCourses);
                } else {
                    break;   /* Break the loop when there are no more courses */
                }
            /* Error handling umd.io courses info error */
            } catch (RestClientException e) {
                System.out.println(ANSI_RED + "[ERROR]: API GET request failed! "
                                + courseUri + ANSI_RESET);
            }

            // No API rate limits, but slow down anyway because we're nice :3
            try {
                Thread.sleep(Math.max((1000 / RETRIEVAL_RATE), 1));
            } catch (InterruptedException t) {
                System.out.println(ANSI_RED + "[ERROR]: Interrupted thread" + ANSI_RESET);
            }
        } while (true);

        return allCourses;  /* Should contain all courses and all their data */
    }

    /* Processing courses after fetching from umd.io */
    private List<Course> refineCourses(List<Course> courses) {
        ArrayList<Course> toBeRemoved = new ArrayList<>();
        for (Course course : courses) {
            /* Filter grad-level courses from the DB (maybe this will change in the future?) */
            Pattern pattern = Pattern.compile("[A-Z]{4}[0-4]\\d{2}[0-9A-Z]?");
            Matcher matcher = pattern.matcher(course.getCourseId());
            if (matcher.find()) {
                /* Fetch average GPA for each remaining course and semesters taught data */
                course.setAverageGPA(fetchGradesData(course));
                ArrayList<String> semestersTaught = fetchSemesterData(course);
                course.setSemesters(semestersTaught);
                course.setNumSemesters(semestersTaught.size());
                System.out.println(ANSI_CYAN + "[DEBUG]: " + course.getCourseId()
                                + " has\n\tPrereqs: " + course.getPrereqs() + "\n\tCoreqs: "
                                + course.getCoreqs() + ANSI_RESET);
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
        String courseUri = UriComponentsBuilder.fromHttpUrl(GRADES_API)
            .queryParam("name", course.getCourseId())
            .toUriString();
        try {
            ResponseEntity<Course> response = restTemplate.exchange(
            courseUri,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Course>() {});

            /* Process the response and return the grade */
            Course fetchedCourse = response.getBody();
            return fetchedCourse == null? 0.0F:fetchedCourse.getAverageGPA();
        /* A lot of courses have no grade data (HTTP 4XX error for those) */
        } catch (HttpClientErrorException d) {
            System.out.println(ANSI_GREEN + "[GRADES]: No grade data for " + course.getCourseId()
                                + "," + " defaulting to 0.0" + ANSI_RESET);
            return 0.0F;
        /* Error handling GET response retrieval error */
        } catch (RestClientException e) {
            System.out.println(ANSI_RED + "[ERROR]: Could not retrieve grades data\n"
                            + "with URI " + courseUri + "\n"
                            + "RestClientException: " + e.getMessage() + ANSI_RESET);
            return 0.0F;
        }
    }

    /* Fetches semester data for a course, returning which semesters it is likely to be taught */
    private ArrayList<String> fetchSemesterData(Course course) {
        int year = Year.now().getValue() - 1;
        ArrayList<String> attempt1 = fetchSemesterDataHelper(course, year);
        ArrayList<String> attempt2 = fetchSemesterDataHelper(course, ++year);

        /* Using two attempts across two recent years for best results I think?
         * Look I know it's goofy but such cases where 1 year is not enough do exist.
         * 2 years should suffice, more would be unnecessary.
        */
        ArrayList<String> result = new ArrayList<>();
        if (attempt1.contains("FALL") | attempt2.contains("FALL")) {
            result.add("FALL");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in FALL" + ANSI_RESET);
        }
        if (attempt1.contains("WINTER") | attempt2.contains("WINTER")) {
            result.add("WINTER");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in WINTER" + ANSI_RESET);
        }
        if (attempt1.contains("SPRING") | attempt2.contains("SPRING")) {
            result.add("SPRING");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in SPRING" + ANSI_RESET);
        }
        if (attempt1.contains("SUMMER") | attempt2.contains("SUMMER")) {
            result.add("SUMMER");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in SUMMER" + ANSI_RESET);
        }
        return result;
    }

    /* Helper function that retrieves semester data from umd.io for a course for a given year */
    private ArrayList<String> fetchSemesterDataHelper(Course course, int year) {
        ArrayList<String> fetchedSemesters = new ArrayList<>();
        HashMap<String, String> possibleSemesters = new HashMap<>();
        String yearStr = String.valueOf(year);
        
        /* Each semester is associated with a year and a two digit number representing month */
        possibleSemesters.put(yearStr + "08", "FALL");
        possibleSemesters.put(yearStr + "12", "WINTER");
        possibleSemesters.put(yearStr + "01", "SPRING");
        possibleSemesters.put(yearStr + "05", "SUMMER");
        String courseId = course.getCourseId();
        
        try {
            possibleSemesters.forEach((possibleSemester, semesterWordForm) -> {
                String courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API + "/" + courseId)
                    .queryParam("semester", possibleSemester)
                    .toUriString();
                try {
                    restTemplate.exchange(
                    courseUri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Course>>() {});
                    fetchedSemesters.add(semesterWordForm);
                /* HTTP 4XX error when semester for that course does not exist */
                } catch (HttpClientErrorException d) {
                    /* Class is not taught during the semester, nothing to worry about */
                }

                try {
                    Thread.sleep(Math.max((1000 / RETRIEVAL_RATE), 1));
                } catch (InterruptedException t) {
                    System.out.println(ANSI_RED + "[ERROR]: Interrupted thread" + ANSI_RESET);
                }
            });
        /* Error handling GET response retrieval error */
        } catch (RestClientException e) {
            System.out.println(ANSI_RED + "[ERROR]: Could not retrieve semester data\n"
                            + "for course " + courseId + "\n"
                            + "RestClientException: " + e.getMessage() + ANSI_RESET);
            return new ArrayList<String>(); // Likely better to return empty if failure occurs
        }
        return fetchedSemesters;
    }
}
