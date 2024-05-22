package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/* Creating a table for relationships. Should be paired with courses from the courses table
 * to show the prerequisites, corequisites, restrictions, and other relationships with other
 * courses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationships {

    /* Retrieved course prerequisites as a string description */
    private String prereqs;

    /* Retrieved course corequisites as a string description */
    private String coreqs;

    /* String description of course restrictions */
    private String restrictions;

    /* String description of credits for what additional courses this course may fulfill */
    private String creditGrantedFor;

    /* Getters */

    public String getPrerequisites() {
        return prereqs;
    }

    public String getCorequisites() {
        return coreqs;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getCreditGrantedFor() {
        return creditGrantedFor;
    }

    /* Setters */

    public void setPrerequisites(String prereqs) {
        this.prereqs = prereqs;
    }

    public void setCorequisites(String coreqs) {
        this.coreqs = coreqs;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void setCreditGrantedFor(String creditGrantedFor) {
        this.creditGrantedFor = creditGrantedFor;
    }

    @Override
    public String toString() {
        return "Prerequisites: " + prereqs.toString() + "\n"
                + "Corequisites: " + coreqs.toString() + "\n"
                + "Restrictions: " + restrictions + "\n"
                + "Credit granted for: " + creditGrantedFor;
    }
}