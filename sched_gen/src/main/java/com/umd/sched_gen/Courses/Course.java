package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** A Course represents a University of Maryland course and defines the Course structure in the
 * courses table of the database */
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

    /* Relationships this course may have with others (like prereqs) */
    @Transient
    @JsonProperty("prereqs")
    private String prereqsString;

    @Transient
    @JsonProperty("coreqs")
    private String coreqsString;

    /* Formatted prereqs and coreqs */
    @JsonIgnore
    private List<Course> prereqs;
    @JsonIgnore
    private List<Course> coreqs;

    /* Restrictions of enrolling in the course (informational purposes only) */
    @JsonProperty("restrictions")
    private String restrictions;

    /* Some courses will grant credit for both itself and other courses */
    @JsonProperty("credit_granted_for")
    private String creditGrantedFor;

    /* Default constructor needed */
    protected Course() {}

    /* Only info needed to retrieve a course's info is its id */
    public Course(int id) {
        this.id = id;
    }

    /* Getters */
    /** Returns the ID (primary key) and generally not useful
     * 
     * @return the unique ID of a Course
     */
    public int getId() {
        return id;
    }

    /** Returns the course ID of a Course, formatted as 4 letters followed by 3 numbers
     * and an optional alphanumeric (ie: CMSC250H)
     * 
     * @return the course ID of a Course
     */
    public String getCourseId() {
        return courseId;
    }

    /** Returns the full name of a Course (ie: "Introduction to Linear Algebra")
     * 
     * @return the name of a Course
    */
    public String getName() {
        return name;
    }

    /** Returns the department ID of a Course (the first 4 letters of the course ID)
     * 
     * @return the department ID of a Course
    */
    public String getDeptId() {
        return deptId;
    }

    /** Returns the number of credits the Course fulfills (usually a number from 1 to 6)
     * 
     * @return the number of credits
    */
    public int getCredits() {
        return credits;
    }

    /** Returns the list of semesters the Course is offered in (FALL, WINTER, SPRING, SUMMER)
     * 
     * @return a list of semesters the Course is offered in
     */
    public ArrayList<String> getSemesters() {
        return new ArrayList<>(semesters);
    }

    /** Returns how many semesters the Course is offered in per year (1-4)
     * 
     * @return size of the list returned from getSemesters()
     */
    public int getNumSemesters() {
        return numSemesters;
    }

    /** Returns the geneds fulfilled by the Course as a list of list of strings. The outmost "layer"
     * represents "or", while the inner one represents an and relationship. Additionally, if a gen
     * ed credit is granted only when taken with another class, this will be represented using a
     * pipe (|) with that class name (ie: "X, Y or Z (if taken with C)" will be returned as
     * [[X, Y], [Z|C]])
     * 
     * @return list of list of strings representing the gen eds fulfillment as described above.
     */
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

    /** Returns the average GPA of the Course
     * 
     * @return average GPA of the Course
     */
    public float getAverageGPA() {
        return averageGPA;
    }

    /** Returns a string description of the prerequisites of the Course
     * 
     * @return the string representing the prerequisites
     */
    public String getPrereqsString() {
        return prereqsString;
    }

    /** Returns a string description of the corequisites of the Course
     * 
     * @return the string representing the corequisites
     */
    public String getCoreqsString() {
        return coreqsString;
    }

    /** Returns a list of Courses that are prerequisites of this Course. Prerequisites are Courses
     * that must be taken in semesters before the current one.
     * 
     * @return a list of Courses that are prerequisites of this Course
     */
    public List<Course> getPrereqs() {
        return new ArrayList<>(prereqs);
    }

    /** Returns a list of Courses that are corequisites of this Course. Corequisites are Courses
     * that must be taken in semesters before or during the current one.
     * 
     * @return a list of Courses that are corequisites of this Course
    */
    public List<Course> getCoreqs() {
        return new ArrayList<>(coreqs);
    }

    /** Returns a string description of the special restrictions of the Course
     * 
     * @return the string representing the restrictions
     */
    public String getRestrictions() {
        return restrictions;
    }

    /** Returns a string description of potential other Courses this Course might fulfill when
     * enrolling it (some Courses may grant credit for both itself and other courses)
     * 
     * @return the string representing the credits granted for taking the Course
     */
    public String getCreditGrantedFor() {
        return creditGrantedFor;
    }

    /* Setters */
    /** Set the values of prereqsString, coreqsString, restrictions, and creditGrantedFor. Intended
     * for use by the API service/data initializer.
     * 
     * @param relationships the Relationship object used to set the fields described above
     * @see the setters for {@link #setCoreqsString(String)}, {@link #setprereqsString(String)},
     * {@link #setRestrictions(String)}, {@link #setCreditGrantedFor(String)}
     */
    @JsonProperty("relationships")
    public void setRelationships(Relationships relationships) {
        this.prereqsString = relationships.getprereqs();
        this.coreqsString = relationships.getcoreqs();
        this.restrictions = relationships.getRestrictions();
        this.creditGrantedFor = relationships.getCreditGrantedFor();
    }

    /** Sets the unique ID value of the Course. Not recommended.
     * 
     * @param id the new ID of the Course to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Sets the course ID of the Course. Course ID should be formatted as 4 letters followed by
     * 3 numbers and an optional alphanumeric (ie: CMSC250H)
     * 
     * @param courseId the new course ID to be set
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /** Sets the full name of a Course (ie: "Introduction to Linear Algebra").
     * 
     * @param name the new name of the Course
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Sets the department ID of a Course (the first 4 letters of the course ID)
     * 
     * @param deptId the new department ID of the Course
     */
    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /** Sets the number of credits the Course fulfills (an integer usually from 1 to 6)
     * 
     * @param credits the new number of credits the Course fulfills
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /** Sets the list of semesters the Course is offered in (FALL, WINTER, SPRING, SUMMER)
     * 
     * @param semesters the new list of semesters the Course is offered in
     */
    public void setSemesters(ArrayList<String> semesters) {
        this.semesters = new ArrayList<>(semesters);
        this.numSemesters = semesters.size();
    }
    
    /** Sets the geneds fulfilled by the Course. New value should be a list of list of strings.
     * The outmost "layer" represents "or", while the inner one represents an and relationship. If
     * a gen ed credit is granted only when taken with another class, this will be represented
     * using a pipe (|) with that class name (ie: "X, Y or Z (if taken with C)" will be returned as
     * [[X, Y], [Z|C]])
     * 
     * @param genEd the new list of list of strings representing the gen eds fulfillment as
     * described above.
    */
    public void setGeneds(List<List<String>> genEd) {
        if (genEd == null) {
            this.geneds = null;
        } else {
            List<List<String>> copy = new ArrayList<>();
            for (List<String> sublist : genEd) {
                copy.add(new ArrayList<>(sublist));     /* Deep copying each sublist */ 
            }
            this.geneds = copy;
        }
    }

    /** Sets the average GPA of the Course, represented as a float with 2 decimal places
     * 
     * @param averageGPA the new average GPA of the Course
    */
    public void setAverageGPA(float averageGPA) {
        this.averageGPA = averageGPA;
    }

    /** Sets a string description of the prerequisites of the Course
     * 
     * @param prereqs the string representing the new prerequisites
    */
    public void setPrereqsString(String prereqs) {
        this.prereqsString = prereqs;
    }

    /** Sets a string description of the corequisites of the Course
     * 
     * @param coreqs the string representing the new corequisites
    */
    public void setCoreqsString(String coreqs) {
        this.coreqsString = coreqs;
    }

    /** Sets a string description of the special restrictions of the Course
     * 
     * @param restrictions the string representing the bew restrictions
     */
    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    /** Sets a string description of potential other Courses this Course might fulfill when
     * enrolling it (some Courses may grant credit for both itself and other courses)
     * 
     * @param creditGrantedFor the string representing the credits granted for taking the Course
     */
    public void setCreditGrantedFor(String creditGrantedFor) {
        this.creditGrantedFor = creditGrantedFor;
    }

    /** Compares the specified object with this Course for equality. Returns true if the specified
     * object is also a Course and has the same Course ID.
     * 
     * @param o object to be compared for equality with this Course
     * @return true if the specified object is equal to this Course
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Course)) {
            return false;
        }
        Course course = (Course)o;
        return courseId.equals(course.getCourseId());
    }
}
