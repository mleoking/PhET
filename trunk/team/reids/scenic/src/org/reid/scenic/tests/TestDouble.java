// Copyright 2002-2011, University of Colorado
package org.reid.scenic.tests;

import fj.data.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author Sam Reid
 */
public class TestDouble {
    static final class X {
        @Override public String toString() {
            return "X";
        }

        @Override public boolean equals( Object obj ) {
            return true;
        }
    }

    static final class Y {
        @Override public boolean equals( Object obj ) {
            return true;
        }
    }

    public static void main( String[] args ) {
        boolean equal = new Vector<Double>().equals( new ArrayList<String>() );
        System.out.println( "equal = " + equal );//prints true

        List<X> a = List.single( new X() );
        List<Y> b = List.single( new Y() );
        System.out.println( "a.equals( b ) = " + a.equals( b ) );

        System.out.println( "a = " + a );

        System.out.println( List.iterableList( Arrays.asList( 1, 3, 4 ) ) );
    }
}
