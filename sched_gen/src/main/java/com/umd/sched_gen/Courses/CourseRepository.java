package com.umd.sched_gen.Courses;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
    List<Course> findByDeptId(String deptId);
    List<Course> findByCourseId(String courseId);
    List<Course> findByName(String name);

    Optional<Course> findById(int Id);
}