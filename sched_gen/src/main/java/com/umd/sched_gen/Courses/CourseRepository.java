package com.umd.sched_gen.Courses;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/* Enables CRUD operations on the courses database */
public interface CourseRepository extends CrudRepository<Course, Integer> {
    List<Course> findByCourseId(String courseId);
    List<Course> findByDeptId(String deptId);
    List<Course> findByName(String name);

    Optional<Course> findById(int Id);
}