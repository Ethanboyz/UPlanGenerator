package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"Course", "Course Name"})
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private int id;

    /* Identifying factors of a course */
    @Column(name = "Course")
    @JsonProperty("course_id")
    private String courseId;

    @Column(name = "Course Name")
    private String name;

    @Column(name = "Department")
    @JsonProperty("dept_id")
    private String deptId;

    /* Info about the course */
    @Column(name = "Credits")
    @JsonProperty("credits")
    private int credits;

    @Column(name = "Gen Eds")
    @JsonProperty("geneds")
    private List<List<String>> geneds;
    
    @Column(name = "Average GPA")
    @JsonProperty("average_gpa")
    private float averageGPA;

    /* 
    private List<Course> prerequisites;
    private List<Course> corequisites; */

    /* Separation of semesters for the sake of DB columns */
    /*
    private boolean fall;
    private boolean winter;
    private boolean spring;
    private boolean summer; */

    /* Default constructor for sake of Spring Data JPA */
    protected Course() {}

    /* Only info needed to retrieve a course's info is its id */
    public Course(int id) {
        this.id = id;
    }

    /* Getters */
    public int getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getDeptId() {
        return deptId;
    }

    public int getCredits() {
        return credits;
    }

    /* Input getter for geneds here */

    public float getAverageGPA() {
        return averageGPA;
    }

    /*
    public List<Course> getPrerequisites() {    
        return new ArrayList<>(prerequisites);
    }

    public List<Course> getCorequisites() {
        return new ArrayList<>(corequisites);
    } */

    /*public boolean getFall() {
        return fall;
    }

    public boolean getWinter() {
        return winter;
    }

    public boolean getSpring() {
        return spring;
    }

    public boolean getSummer() {
        return summer;
    } */

    /* Setters */
    public void setId(int id) {
        this.id = id;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    /* Input setter for geneds here */

    public void setAverageGPA(float averageGPA) {
        this.averageGPA = averageGPA;
    }

    /*
    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = new ArrayList<>(prerequisites);
    }

    public void setCorequisites(List<Course> corequisites) {
        this.corequisites = new ArrayList<>(corequisites);
    } */

    /* public void setFall(boolean fall) {
        this.fall = fall;
    }

    public void setWinter(boolean winter) {
        this.winter = winter;
    }

    public void setSpring(boolean spring) {
        this.spring = spring;
    }

    public void setSummer(boolean summer) {
        this.summer = summer;
    } */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Course)) {
            return false;
        }
        Course course = (Course)o;
        return courseId.equals(course.getCourseId());
    }

    @Override
    public String toString() {
        return "Course:" + courseId;
    }
}
