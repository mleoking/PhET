// Copyright 2002-2011, University of Colorado
package org.reid.scenic.tests;

import fj.data.List;

import java.util.Arrays;

import static fj.data.List.iterableList;

interface Predicate<T> {
    public boolean apply( T t );
}

class Worker {
    public boolean isEmployee() {
        return false;
    }
}

public class TestList {
    public static void main( String[] args ) {
        testListEquality();

        testListEqualityWithDifferentTypedMembers();

        Predicate<Worker> isEmployeePredicate = new Predicate<Worker>() {
            public boolean apply( Worker w ) {
                return w.isEmployee();
            }
        };
    }

    private static void testListEqualityWithDifferentTypedMembers() {
        final java.util.List<Integer> x = Arrays.asList( 1, 2, 3 );
        final java.util.List<String> y = Arrays.asList( "a","b","c");
        final List<Integer> a = iterableList( x );
        final List<String> b = iterableList( y );
        //Look ma, no type exception
        boolean equal = a.equals( b );
        System.out.println( "equal = " + equal );//prints false
    }

    private static void testListEquality() {
        final java.util.List<Integer> myList = Arrays.asList( 1, 2, 3 );
        final List<Integer> a = iterableList( myList );
        final List<Integer> b = iterableList( myList );
        boolean equal = a.equals( b );
        System.out.println( "equal = " + equal );//prints false
    }
}
