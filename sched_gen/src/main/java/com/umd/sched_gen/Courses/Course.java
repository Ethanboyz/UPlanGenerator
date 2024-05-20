package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.util.ArrayList;
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

    /* The course code primarily used for identification (4 letters followed by 3 numbers and an
     * optional alphanumeric) */
    @Column(name = "Course")
    @JsonProperty("course_id")
    private String courseId;

    /* The full name of the course (eg: Advanced Data Structures) */
    @Column(name = "Course Name")
    private String name;

    /* The 4 letter code of the department (eg: CMSC, ENGL) */
    @Column(name = "Department")
    @JsonProperty("dept_id")
    private String deptId;

    /* Info about the course */
    @Column(name = "Credits")
    @JsonProperty("credits")
    private int credits;

    /* Which semesters of the year is the course usually taught */
    @Column(name = "Semesters")
    ArrayList<String> semesters;

    /* Length of semesters list */
    @Column(name = "# of Semesters")
    private int numSemesters;

    /* Strings representing the General Education requirements the course fulfills. Note that this
     * is an array of arrays of strings. The outmost "layer" represents "or", while the inner one
     * represents an and relationship. Additionally, if a gen ed credit is granted only when taken
     * with another class, this will be represented using a pipe (|) with that class name.
     * 
     * For instance, "X, Y or Z (if taken with C)" will be returned as [[X, Y], [Z|C]] here.
     * AOSC200 is also a good example course of this. */
    @Column(name = "Gen Eds")
    @JsonProperty("gen_ed")
    private List<List<String>> geneds;
    
    /* Average GPA data from planetterp */
    @Column(name = "Average GPA")
    @JsonProperty("average_gpa")
    private float averageGPA;

    /* Relationships this course may have with others (like prereqs). These will be obtained via
     * web scraping, unlike the others which are obtained via API GETs.
    */
    @Column(name = "Prerequisites")
    private String prereqs;     /* Courses that must be taken before this course */

    @Column(name = "Corequisites")
    private String coreqs;      /* Courses that can (and must) be taken before or alongside this course */

    @Column(name = "Restrictions")
    private String restrictions;

    @Column(name = "Credit Granted For")
    private String creditGrantedFor;

    /* Default constructor needed */
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

    public ArrayList<String> getSemesters() {
        return new ArrayList<>(semesters);
    }

    public int getNumSemesters() {
        return numSemesters;
    }

    public List<List<String>> getGeneds() {
        if (geneds == null) {
            return null;
        }
        List<List<String>> copy = new ArrayList<>();
        for (List<String> sublist : geneds) {
            copy.add(new ArrayList<>(sublist));
        }
        return copy;
    }

    public float getAverageGPA() {
        return averageGPA;
    }

    public String getPrereqs() {
        return prereqs;
    }

    public String getCoreqs() {
        return coreqs;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getCreditGrantedFor() {
        return creditGrantedFor;
    }

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

    public void setSemesters(ArrayList<String> semesters) {
        this.semesters = new ArrayList<>(semesters);
    }

    public void setNumSemesters(int numSemesters) {
        this.numSemesters = numSemesters;
    }
    
    public void setGeneds(List<List<String>> genEd) {
        if (genEd == null) {
            this.geneds = null;
        } else {
            List<List<String>> copy = new ArrayList<>();
            for (List<String> sublist : genEd) {
                copy.add(new ArrayList<>(sublist)); // Deep copying each sublist
            }
            this.geneds = copy;
        }
    }

    public void setAverageGPA(float averageGPA) {
        this.averageGPA = averageGPA;
    }

    public void setPrereqs(String prereqs) {
        this.prereqs = prereqs;
    }

    public void setCoreqs(String coreqs) {
        this.coreqs = coreqs;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void setCreditGrantedFor(String creditGrantedFor) {
        this.creditGrantedFor = creditGrantedFor;
    }

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
