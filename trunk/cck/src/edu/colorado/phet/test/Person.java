package edu.colorado.phet.test;

import java.util.Date;

/**
 * An simple person class
 * Testing cvs.
 * Testing.
 */
public class Person implements java.io.Serializable {

    /**
     * The name of the person
     */
    private String name = null;

    /**
     * The Date of birth
     */
    private Date dob = null;

    /**
     * Creates a Person with no name
     */
    public Person() {
        super();
    }

    /**
     * Creates a Person with the given name
     */
    public Person(String name) {
        this.name = name;
    }

    /**
     * @return date of birth of the person
     */
    public Date getDateOfBirth() {
        return dob;
    }

    /**
     * @return name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the date of birth of the person
     * @param dob the dob of the person
     */
    public void setDateOfBirth(Date dob) {
        this.dob = dob;
    }

    /**
     * Sets the name of the person
     * @param name the name of the person
     */
    public void setName(String name) {
        this.name = name;
    }
}
