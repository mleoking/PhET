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
        final Property<Double> radius = new Property<Double>( 1.0 );
        final Property<Double> angle = new Property<Double>( Math.toRadians( 45 ) );

        final Property<Double> x = new Property<Double>( Math.cos( angle.get() ) * radius.get() );
        final Property<Double> y = new Property<Double>( Math.sin( angle.get() ) * radius.get() );

        //When radius or angle changes, update x and y
        final SimpleObserver updateXY = new SimpleObserver() {
            public void update() {
                x.set( Math.cos( angle.get() ) * radius.get() );
                y.set( Math.sin( angle.get() ) * radius.get() );
            }
        };
        radius.addObserver( updateXY );
        angle.addObserver( updateXY );

        //When x or y changes, update the radius and angle
        SimpleObserver updateRA = new SimpleObserver() {
            public void update() {
                radius.set( Math.sqrt( x.get() * x.get() + y.get() * y.get() ) );
                angle.set( Math.atan2( y.get(), x.get() ) );
                System.out.println( "radius = " + radius );
            }
        };
        x.addObserver( updateRA );
        y.addObserver( updateRA );

        radius.set( 3.0 );

        System.out.println( "radius.get() = " + radius.get() );
    }
}