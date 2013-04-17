// Copyright 2002-2012, University of Colorado
package com.pixelzoom.examples;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
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

        // Save this so we can compare results at the end.
        final double initialAngle = Math.toRadians( 45 );

        // Polar & Cartesian representations, which we wish to keep synchronized.
        final Property<PolarCoordinates> polarCoordinates = new Property<PolarCoordinates>( new PolarCoordinates( 1.0, initialAngle ) );
        final Property<CartesianCoordinates> cartesianCoordinates =
                new Property<CartesianCoordinates>( new CartesianCoordinates( PolarCartesianConverter.getX( polarCoordinates.get().radius, polarCoordinates.get().angle ),
                                                                              PolarCartesianConverter.getY( polarCoordinates.get().radius, polarCoordinates.get().angle ) ) );

        PolarCartesianConverter.getX( polarCoordinates.get().radius, polarCoordinates.get().angle );

        // When polar coordinates change, update Cartesian coordinates.
        polarCoordinates.addObserver( new VoidFunction1<PolarCoordinates>() {
            public void apply( PolarCoordinates polarCoordinates ) {
                CartesianCoordinates newCartesianCoordinates = new CartesianCoordinates( PolarCartesianConverter.getX( polarCoordinates.radius, polarCoordinates.angle ),
                                                                                         PolarCartesianConverter.getY( polarCoordinates.radius, polarCoordinates.angle ) );
                if ( !newCartesianCoordinates.equals( cartesianCoordinates.get() ) ) {
                    cartesianCoordinates.set( newCartesianCoordinates );
                }
                System.out.println( "cartesianCoordinates=" + cartesianCoordinates.get().toString() );
            }
        } );

        // When Cartesian coordinates change, update polar coordinates.
        cartesianCoordinates.addObserver( new VoidFunction1<CartesianCoordinates>() {
            public void apply( CartesianCoordinates cartesianCoordinates ) {
                PolarCoordinates newPolarCoordinates = new PolarCoordinates( PolarCartesianConverter.getRadius( cartesianCoordinates.x, cartesianCoordinates.y ),
                                                                             PolarCartesianConverter.getAngle( cartesianCoordinates.x, cartesianCoordinates.y ) );
                if ( !newPolarCoordinates.equals( polarCoordinates.get() ) ) {
                    polarCoordinates.set( newPolarCoordinates );
                }
                System.out.println( "polarCoordinates=" + polarCoordinates.get().toString() );
            }
        } );

        // Change just the radius.
        polarCoordinates.set( new PolarCoordinates( 3.0, polarCoordinates.get().angle ) );

        // Compare actual and expected results. There will be a small error (in this case, in the angle) due to precision of double.
        System.out.println( "Polar coordinates: " );
        System.out.println( "  actual = " + polarCoordinates.get().toString() );
        System.out.println( "  expected = " + new PolarCoordinates( polarCoordinates.get().radius, initialAngle ).toString() );
        System.out.println( "Cartestian coordinates: " );
        System.out.println( "  actual = " + cartesianCoordinates.get().toString() );
        System.out.println( "  expected = " + new CartesianCoordinates( PolarCartesianConverter.getX( polarCoordinates.get().radius, polarCoordinates.get().angle ),
                                                                        PolarCartesianConverter.getY( polarCoordinates.get().radius, polarCoordinates.get().angle ) ).toString() );
    }

}
