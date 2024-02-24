package com.umd.sched_gen;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.umd.sched_gen.Courses.Course;

import java.util.List;
import java.util.ArrayList;


@Service
public class ApiService {
    private final String API = "https://api.umd.io/v1/courses";
    private final int COURSES_PER_PAGE = 100;
    private final RestTemplate restTemplate;

    public ApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Course> fetchAllCourses() {
        List<Course> allCourses = new ArrayList<>();
        int page = 1;
        ResponseEntity<List<Course>> response;

        do {
            /* umd.io returns paginated list of courses, must traverse through pages */
            String url = UriComponentsBuilder.fromHttpUrl(API)
                .queryParam("page", page++)
                .queryParam("per_page", COURSES_PER_PAGE)
                .toUriString();
            try {
                response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Course>>() {});
            
            List<Course> courses = response.getBody();
            if (courses != null && !courses.isEmpty()) {
                allCourses.addAll(courses);
            } else {
                break;
            }
            } catch (Exception e) {
                System.out.println("API GET request failed!");
            }
        } while (true);

        return allCourses;
    }
}
