package com.umd.sched_gen;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, String> {
    List<Course> findByDepartment(String department);

    Optional<Course> findById(String id);
}