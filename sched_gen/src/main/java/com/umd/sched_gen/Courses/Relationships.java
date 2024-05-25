package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A relationship is a series of fields that hold information of relationships between Courses.
 * This includes prerequisites, corequisites, and restrictions and are primarily used to enable
 * the API service to fetch the needed relationship data of Courses.
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
    /** Returns a string description of the prerequisites of the Course
     * 
     * @return the string representing the prerequisites
     */
    public String getprereqs() {
        return prereqs;
    }

    /** Returns a string description of the corequisites of the Course
     * 
     * @return the string representing the corequisites
     */
    public String getcoreqs() {
        return coreqs;
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
    /** Sets a string description of the prerequisites of the Course
     * 
     * @param prereqs the string representing the new prerequisites
    */
    public void setprereqs(String prereqs) {
        this.prereqs = prereqs;
    }

    /** Sets a string description of the corequisites of the Course
     * 
     * @param coreqs the string representing the new corequisites
    */
    public void setcoreqs(String coreqs) {
        this.coreqs = coreqs;
    }

    /** Sets a string description of the special restrictions of the Course
     * 
     * @param restrictions the string representing the bew restrictions
     */
    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void setCreditGrantedFor(String creditGrantedFor) {
        this.creditGrantedFor = creditGrantedFor;
    }

    /** Sets a string description of potential other Courses this Course might fulfill when
     * enrolling it (some Courses may grant credit for both itself and other courses)
     * 
     * @param creditGrantedFor the string representing the credits granted for taking the Course
     */
    @Override
    public String toString() {
        return "Prerequisites: " + prereqs.toString() + "\n"
                + "Corequisites: " + coreqs.toString() + "\n"
                + "Restrictions: " + restrictions + "\n"
                + "Credit granted for: " + creditGrantedFor;
    }
}