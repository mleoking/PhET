// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests.testabstraction;

/**
 * @author Sam Reid
 */
public class TestSelfType {
    private static class Slice<T extends Slice<T>> {
        public final double x;

        private Slice( double x ) {this.x = x;}

//        public T translate( double dx ) {return new T( x + dx );}//Compile error
    }

    private static class RotatableSlice {
        public final double degrees;

        private RotatableSlice( double degrees ) {this.degrees = degrees;}

        public RotatableSlice rotate( double degrees ) { return new RotatableSlice( degrees ); }
    }

    public static void main( String[] args ) {
        RotatableSlice r = new RotatableSlice( 0.0 );
        RotatableSlice r2 = r.rotate( 90 );
//        RotatableSlice r3 = r2.translate(100);//Compile error
    }
}
