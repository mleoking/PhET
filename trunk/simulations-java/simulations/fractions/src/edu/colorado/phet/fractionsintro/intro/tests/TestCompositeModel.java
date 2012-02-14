// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * model: numerator / denominator * 100 = percentage
 * same as
 * numerator = percentage /100 * denominator
 *
 * @author Sam Reid
 */
public class TestCompositeModel {
    public static void main( String[] args ) {
        //Radius/angle representation
        final Property<Double> radius = new Property<Double>( 1.0 );
        final Property<Double> angle = new Property<Double>( Math.toRadians( 45 ) );

        //Cartesian representation
        final Property<Double> x = new Property<Double>( Math.cos( angle.get() ) * radius.get() );
        final Property<Double> y = new Property<Double>( Math.sin( angle.get() ) * radius.get() );

        //When radius or angle changes, update x and y
        final SimpleObserver updateXY = new SimpleObserver() {
            public void update() {
                final double newX = Math.cos( angle.get() ) * radius.get();
                System.out.println( "newX = " + newX );
                x.set( newX );

                final double newY = Math.sin( angle.get() ) * radius.get();
                System.out.println( "newY = " + newY );
                y.set( newY );
            }
        };
        radius.addObserver( updateXY );
        angle.addObserver( updateXY );

        //When x or y changes, update the radius and angle
        SimpleObserver updateRA = new SimpleObserver() {
            public void update() {
                final double newRadius = Math.sqrt( x.get() * x.get() + y.get() * y.get() );
                System.out.println( "newRadius = " + newRadius );
                radius.set( newRadius );
                final double newAngle = Math.atan2( y.get(), x.get() );
                System.out.println( "newAngle = " + newAngle );
                angle.set( newAngle );
            }
        };
        x.addObserver( updateRA );
        y.addObserver( updateRA );

        radius.set( 3.0 );

        System.out.println( "radius.get() = " + radius.get() );
    }
}