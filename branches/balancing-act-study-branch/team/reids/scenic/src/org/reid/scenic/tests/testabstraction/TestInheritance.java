// Copyright 2002-2012, University of Colorado
package org.reid.scenic.tests.testabstraction;

/**
 * @author Sam Reid
 */
public class TestInheritance {
    private static class Slice {
        public final double x;

        private Slice( double x ) {this.x = x;}

        public Slice translate( double dx ) {return new Slice( x + dx );}
    }

    private static class RotatableSlice {
        public final double degrees;

        private RotatableSlice( double degrees ) {this.degrees = degrees;}

        public RotatableSlice rotate( double degrees ) { return new RotatableSlice( degrees ); }
    }

    public static void main( String[] args ) {
        RotatableSlice r = new RotatableSlice( 0.0 );
        RotatableSlice r2 = r.rotate( 90 );
//        RotatableSlice r3 = r2.translate( 100 );//Compile error
    }
}
