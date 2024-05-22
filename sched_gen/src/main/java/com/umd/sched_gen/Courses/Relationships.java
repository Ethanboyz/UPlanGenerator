package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Creating a table for relationships. Should be paired with courses from the courses table
 * to show the prerequisites, corequisites, restrictions, and other relationships with other
 * courses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationships {

    /* Retrieved course prerequisites as a string description */
    @JsonProperty("prereqs")
    private String prereqs;

    /* Retrieved course corequisites as a string description */
    @JsonProperty("coreqs")
    private String coreqs;

    /* String description of course restrictions */
    @JsonProperty("restrictions")
    private String restrictions;

    /* String description of credits for what additional courses this course may fulfill */
    @JsonProperty("credit_granted_for")
    private String creditGrantedFor;

    /* Getters */

    public String getprereqs() {
        return prereqs;
    }

    public String getcoreqs() {
        return coreqs;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getCreditGrantedFor() {
        return creditGrantedFor;
    }

    /* Setters */

    public void setprereqs(String prereqs) {
        this.prereqs = prereqs;
    }

    public void setcoreqs(String coreqs) {
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