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
        final java.util.List<Integer> myList = Arrays.asList( 1, 2, 3 );
        final List<Integer> a = iterableList( myList );
        final List<Integer> b = iterableList( myList );
        boolean equal = a.equals( b );
        System.out.println( "equal = " + equal );//prints false


        Predicate<Worker> isEmployeePredicate = new Predicate<Worker>() {
            public boolean apply( Worker w ) {
                return w.isEmployee();
            }
        };
    }
}
