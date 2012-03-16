// Copyright 2002-2012, University of Colorado
package org.reid.scenic.tests.testabstraction;

/**
 * @author Sam Reid
 */
public class TestCompositionI {
    private static class Slice {
        public final double x;

        private Slice( double x ) {this.x = x;}

        public Slice translate( double dx ) {return new Slice( x + dx );}
    }

    private static interface ISlice {
    }

    private static class RotatableSlice implements ISlice {
        private final Slice slice;
        private final double angle;

        private RotatableSlice( double angle ) {
            this.slice = new Slice( 0 );
            this.angle = angle;
        }

        private RotatableSlice( Slice slice, double angle ) {
            this.slice = slice;
            this.angle = angle;
        }

        public RotatableSlice rotate( int i ) {
            return new RotatableSlice( slice, i );
        }

        public RotatableSlice translate( int dx ) {
            return new RotatableSlice( slice.translate( dx ), angle );
        }
    }

    public static void main( String[] args ) {
        RotatableSlice r = new RotatableSlice( 0.0 );
        RotatableSlice r2 = r.rotate( 90 );
        RotatableSlice r3 = r2.translate( 100 );
    }
}