/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.test;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 8:14:25 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class PersonList {
    ArrayList people = new ArrayList();

    public PersonList() {
    }

    public ArrayList getPeople() {
        return people;
    }

    public void setPeople(ArrayList people) {
        this.people = people;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < people.size(); i++) {
            Person person = (Person) people.get(i);
            sb.append("Person[" + i + "]=" + person.getName());
        }
        return sb.toString();
    }
}
