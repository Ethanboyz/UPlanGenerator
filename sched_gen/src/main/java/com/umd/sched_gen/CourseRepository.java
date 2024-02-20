package com.umd.sched_gen;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends CrudRepository<Course, UUID> {
    List<Course> findByDeptId(String deptId);
    List<Course> findByCourseId(String courseId);
    List<Course> findByName(String name);

    Optional<Course> findById(UUID courseId);
}