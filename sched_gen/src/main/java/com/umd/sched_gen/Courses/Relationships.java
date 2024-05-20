package com.umd.sched_gen.Courses;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "relationships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {})
})
/* Creating a table for relationships. Should be paired with courses from the courses table
 * to show the prerequisites, corequisites, restrictions, and other relationships with other
 * courses.
 */
public class Relationships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private int id;

    /* List of course prerequisites */
    @Column(name = "Prereqs")
    @JsonProperty("prereqs")
    private List<Course> prerequisites;

    /* List of course corequisites */
    @Column(name = "Coreqs")
    @JsonProperty("coreqs")
    private List<Course> corequisites;

    /* String description of course restrictions */
    @Column(name = "Restrictions")
    @JsonProperty("coreqs")
    private String restrictions;

    /* String description of credits for what additional courses this course may fulfill */
    @Column(name = "Credit Granted For")
    @JsonProperty("credit_granted_for")
    private String credit_granted_for;

    public Relationships(Relationships relationship) {
        this.prerequisites = relationship.getPrerequisites();
        this.corequisites = relationship.getCorequisites();
        this.restrictions = relationship.getRestrictions();
        this.credit_granted_for = relationship.getCredit_granted_for();
    }

    protected Relationships() {}

    /* Getters */
    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public List<Course> getCorequisites() {
        return corequisites;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getCredit_granted_for() {
        return credit_granted_for;
    }

    /* Setters */
    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setCorequisites(List<Course> corequisites) {
        this.corequisites = corequisites;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void setCredit_granted_for(String credit_granted_for) {
        this.credit_granted_for = credit_granted_for;
    }

    @Override
    public String toString() {
        return "Prerequisites: " + prerequisites.toString() + "\n"
                + "Corequisites: " + corequisites.toString() + "\n"
                + "Restrictions: " + restrictions + "\n"
                + "Credit granted for: " + credit_granted_for;
    }
}