// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

/**
 * @author Sam Reid
 */
public class Person {
    public final String name;
    public final int age;

    public Person( int age, String name ) {
        this.age = age;
        this.name = name;
    }

    public Person name( String name ) {
        return new Person( age, name );
    }
}
