package com.umd.sched_gen.DataInitializer;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Year;

/** This service manages all API operations to extract and refine course data for storage. */
@Service
public class ApiService {
    private final String COURSES_API = "https://api.umd.io/v1/courses";
    private final String COURSES_API_MINIFIED = "https://api.umd.io/v1/courses/list";
    private final int COURSES_PER_PAGE = 100;   /* Up to 100 */
    private final String GRADES_API = "https://planetterp.com/api/v1/course";
    private final int RETRIEVAL_RATE = 5;       /* Up to 1000 */
    private final RestTemplate restTemplate;
    private int PREV_YEAR = Year.now().getValue() - 1;

    /* Colors for some nice printing! */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public ApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /** Fetch all Courses data from umd.io and planetterp.com. Use to populate the database.
     * 
     * @return a list of all undergraduate UMD courses to add to database.
    */
    public List<Course> fetchAllCourses() {
        List<Course> allCourses = new ArrayList<>();

        ArrayList<String> possibleSemesters = new ArrayList<>();
        ArrayList<List<Course>> semesterCourses = coursesPerSemester();
        String yearStr = String.valueOf(PREV_YEAR);
        
        /* Each semester is associated with a year and a two digit number representing month */
        possibleSemesters.add(yearStr + "08");  /* Fall */
        possibleSemesters.add(yearStr + "12");  /* Winter */
        possibleSemesters.add(yearStr + "01");  /* Spring */
        possibleSemesters.add(yearStr + "05");  /* Summer */

        for (String possibleSemester : possibleSemesters) {
            int page = 1;
            System.out.println(ANSI_YELLOW + "[NOTICE]: Now processing semester " + possibleSemester
                                + ANSI_RESET);
            do {    /* umd.io returns paginated list of courses, must traverse through pages */
                /* Build the request for umd.io */
                String courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API)
                    .queryParam("page", page++)
                    .queryParam("per_page", Math.min(COURSES_PER_PAGE, 100))
                    .queryParam("semester", possibleSemester)
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
                        fetchedCourses = refineCourses(fetchedCourses, allCourses, semesterCourses);
                        allCourses.addAll(fetchedCourses);
                    } else {
                        break;   /* Move on to next semester when no more courses for current one */
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
        }

        return allCourses;  /* Should contain all courses and all their data */
    }

    /** Processing Courses after fetching from umd.io only if they aren't duplicates from allCourses
     * 
     * @param courses the list of Courses to refine
     * @param allCourses some Courses will not be returned if they are included in allCourses to prevent
     * duplicates
     * @param semesterCourses contains a list of lists of Courses representing each semester of the
     * year previous to the current one, starting from the fall to the summer
     * (ie: fall 2023 - summer 2023), and which courses are offered during each semester.
     * @return a list of refined Courses.
    */
    private List<Course> refineCourses(List<Course> courses, List<Course> allCourses, List<List<Course>> semesterCourses) {
        ArrayList<Course> toBeRemoved = new ArrayList<>();
        for (Course course : courses) {
            /* Will not process duplicate courses */
            if (allCourses.contains(course)) {
                System.out.println(ANSI_YELLOW + "[NOTICE]: Cannot add " + course.getCourseId()
                                + " (already been added)!" + ANSI_RESET);
                toBeRemoved.add(course);
                continue;
            };
            /* Filter GRAD-LEVEL courses from the DB */
            Pattern pattern = Pattern.compile("[A-Z]{4}[0-4]\\d{2}[0-9A-Z]?");
            Matcher matcher = pattern.matcher(course.getCourseId());
            if (matcher.find()) {
                /* Fetch average GPA for each remaining course and semesters taught data */
                course.setAverageGPA(fetchGradesData(course));
                ArrayList<String> semestersTaught = fetchSemesterData(course, semesterCourses);
                course.setSemesters(semestersTaught);
                System.out.println(ANSI_CYAN + "[DEBUG]: course " + course.getCourseId() + "\n"
                                    + "Prereqs: " + course.getPrereqsString() + "\n"
                                    + "Coreqs: " + course.getCoreqsString() + "\n"
                                    + "Restrictions: " + course.getRestrictions() + "\n"
                                    + "Credits granted for: " + course.getCreditGrantedFor());
            } else {
                toBeRemoved.add(course);
                System.out.println(ANSI_YELLOW + "[NOTICE]: Cannot add " + course.getCourseId()
                                + " (not undergrad level)!" + ANSI_RESET);
            }
        }
        courses.removeAll(toBeRemoved);
        return courses;
    }

    /** Retrieves average GPA data from planetterp.com for a Course.
     * @param course the Course to fetch grades data for
     * @return the average GPA of the Course
    */
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

    /** Fetches semester data for a Course, returning which semesters it is likely to be taught.
     * 
     * @param course the Course to fetch semester data for
     * @param semesterCourses contains a list of lists of Courses representing each semester of the
     * year previous to the current one, starting from the fall to the summer
     * (ie: fall 2023 - summer 2023), and which courses are offered during each semester.
     * @return a list of all semesters the Course is likely to be offered in
    */
    private ArrayList<String> fetchSemesterData(Course course, List<List<Course>> semesterCourses) {
        /* Using two attempts across two recent years for best results I think?
         * For now, we will evaluate the semesters in which a course is taken using the data
         * of one year.
        */
        ArrayList<String> result = new ArrayList<>();
        if (semesterCourses.get(0).contains(course)) {
            result.add("FALL");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in FALL" + ANSI_RESET);
        }
        if (semesterCourses.get(1).contains(course)) {
            result.add("WINTER");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in WINTER" + ANSI_RESET);
        }
        if (semesterCourses.get(2).contains(course)) {
            result.add("SPRING");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in SPRING" + ANSI_RESET);
        }
        if (semesterCourses.get(3).contains(course)) {
            result.add("SUMMER");
            System.out.println(ANSI_GREEN + "[SEMESTERS]: " + course.getCourseId()
                            + " taught in SUMMER" + ANSI_RESET);
        }
        return result;
    }

    /** Constructs a list of Courses per semester from umd.io for a given year and aggregates the
     * lists into one.
     * 
     * @return a list of lists of Courses representing each semester of the
     * year previous to the current one, starting from the fall to the summer
     * (ie: fall 2023 - summer 2023), and which courses are offered during each semester.
     */
    private ArrayList<List<Course>> coursesPerSemester() {
        ArrayList<List<Course>> semesterCourses = new ArrayList<>();
        String yearStr = String.valueOf(PREV_YEAR);
        
        /* Each semester is associated with a year and a two digit number representing month */
        String fall = yearStr + "08";
        String winter = yearStr + "12";
        String spring = yearStr + "01";
        String summer = yearStr + "05";

        List<Course> fallCourses;
        List<Course> winterCourses;
        List<Course> springCourses;
        List<Course> summerCourses;
        
        try {
            String courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API_MINIFIED)
                .queryParam("semester", fall)
                .toUriString();
            ResponseEntity<List<Course>> response = restTemplate.exchange(
            courseUri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Course>>() {});
            fallCourses = response.getBody();

            courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API_MINIFIED)
                .queryParam("semester", winter)
                .toUriString();
            response = restTemplate.exchange(courseUri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Course>>() {});
            winterCourses = response.getBody();

            courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API_MINIFIED)
                .queryParam("semester", spring)
                .toUriString();
            response = restTemplate.exchange(courseUri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Course>>() {});
            springCourses = response.getBody();

            courseUri = UriComponentsBuilder.fromHttpUrl(COURSES_API_MINIFIED)
                .queryParam("semester", summer)
                .toUriString();
            response = restTemplate.exchange(courseUri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Course>>() {});
            summerCourses = response.getBody();

        /* Error handling GET response retrieval error */
        } catch (RestClientException e) {
            System.out.println(ANSI_RED + "[ERROR]: Could not retrieve semester data\n"
                            + "for semesters\n"
                            + "RestClientException: " + e.getMessage() + ANSI_RESET);
            return new ArrayList<List<Course>>(); // Likely better to return empty if failure occurs
        }
        semesterCourses.add(fallCourses);
        semesterCourses.add(winterCourses);
        semesterCourses.add(springCourses);
        semesterCourses.add(summerCourses);
        return semesterCourses;
    }
}
