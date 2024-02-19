
package com.umd.sched_gen;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "course_id", updatable = false, nullable = false)
    private UUID courseId;
    private String id;

    /* Identifying factors of a course */
    private String department;

    /* info about the course */
    /*private float averageGPA; */

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
    public Course(String id) {
        this.id = id;
    }

    /* Getters */
    public String getId() {
        return id;
    }

    public String getDepartment() {
        return department;
    }

    /*public float getAverageGPA() {
        return averageGPA;
    } */

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
    public void setId(String id) {
        this.id = id;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /* public void setAverageGPA(float averageGPA) {
        this.averageGPA = averageGPA;
    } */

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
        return id.equals(course.id);
    }

    @Override
    public String toString() {
        return "Course:" + id;
    }
}
