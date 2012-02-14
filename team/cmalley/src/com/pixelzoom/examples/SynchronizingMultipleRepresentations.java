// Copyright 2002-2012, University of Colorado
package com.pixelzoom.examples;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Demonstrate fix for SR's "multiple representations" problem.
 * In SR's example, it was impossible to keep Cartesian and polar coordinate representations properly
 * synchronized because the representations were not atomic; that is, changes in some part of the
 * one representation would trigger changes in some part of the other representation, leading to
 * incorrect values. The fix is to ensure that the representations are atomic. Note that this problem
 * is somewhat independent of Property<T>, it could just as easily happen with Listeners if your
 * data representation is not atomic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SynchronizingMultipleRepresentations {

    // This replaces separate Property<Double> for x and y coordinates.
    public static class PolarCoordinates {
        public final double radius;
        public final double angle;

        public PolarCoordinates( double radius, double angle ) {
            this.radius = radius;
            this.angle = angle;
        }

        public String toString() {
            return "[" + radius + "," + angle + "]";
        }

        @Override public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }
            PolarCoordinates that = (PolarCoordinates) o;
            if ( Double.compare( that.angle, angle ) != 0 ) { return false; }
            if ( Double.compare( that.radius, radius ) != 0 ) { return false; }
            return true;
        }
    }

    // This replaces separate Property<Double> for radius and angle coordinates.
    public static class CartesianCoordinates {
        public final double x;
        public final double y;

        public CartesianCoordinates( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "[" + x + "," + y + "]";
        }

        @Override public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }
            CartesianCoordinates that = (CartesianCoordinates) o;
            if ( Double.compare( that.x, x ) != 0 ) { return false; }
            if ( Double.compare( that.y, y ) != 0 ) { return false; }
            return true;
        }
    }

    public static void main( String[] args ) {

        // Polar & Cartesian representations, which we wish to keep synchronized.
        final Property<PolarCoordinates> polarCoordinates = new Property<PolarCoordinates>( new PolarCoordinates( 1.0, Math.toRadians( 45 ) ) );
        final Property<CartesianCoordinates> cartesianCoordinates = new Property<CartesianCoordinates>(
                new CartesianCoordinates( Math.cos( polarCoordinates.get().angle ) * polarCoordinates.get().radius, Math.sin( polarCoordinates.get().angle ) * polarCoordinates.get().radius ) );

        // When polar coordinates changes, update Cartesian coordinates.
        polarCoordinates.addObserver( new VoidFunction1<PolarCoordinates>() {
            public void apply( PolarCoordinates polar ) {
                CartesianCoordinates newCartesianCoordinates = new CartesianCoordinates( Math.cos( polar.angle ) * polar.radius, Math.sin( polar.angle ) * polar.radius );
                if ( !newCartesianCoordinates.equals( cartesianCoordinates.get() ) ) {
                    cartesianCoordinates.set( newCartesianCoordinates );
                }
                System.out.println( "cartesianCoordinates=" + cartesianCoordinates.get().toString() );
            }
        } );

        // When Cartesian coordinates changes, update polar coordinates.
        cartesianCoordinates.addObserver( new VoidFunction1<CartesianCoordinates>() {
            public void apply( CartesianCoordinates cartesian ) {
                PolarCoordinates newPolarCoordinates = new PolarCoordinates( Math.sqrt( cartesian.x * cartesian.x + cartesian.y * cartesian.y ), Math.atan2( cartesian.y, cartesian.x ) );
                if ( !newPolarCoordinates.equals( polarCoordinates.get() ) ) {
                    polarCoordinates.set( newPolarCoordinates );
                }
                System.out.println( "polarCoordinates=" + polarCoordinates.get().toString() );
            }
        } );

        polarCoordinates.set( new PolarCoordinates( 3.0, polarCoordinates.get().angle ) );
        System.out.println( "radius.get() = " + polarCoordinates.get().radius );
    }

}
