// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class WaveSensor {
    public final Probe probe1 = new Probe( -4.173076923076922E-7, 9.180769230769231E-7 );
    public final Probe probe2 = new Probe( -1.5440384615384618E-6, -1.2936538461538458E-6 );
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 4.882500000000015E-6, -3.1298076923077013E-6 ) );

    public WaveSensor() {
        //This code helps to come up with a good set of defaults for the values.
//        final SimpleObserver simpleObserver = new SimpleObserver() {
//            public void update() {
//                System.out.println( "bodyPosition.setValue( " + toCode( bodyPosition.getValue() ) + ");" );
//                System.out.println( "probe1.position.setValue( " + toCode( probe1.position.getValue() ) + ");" );
//                System.out.println( "probe2.position.setValue( " + toCode( probe2.position.getValue() ) + ");" );
//            }
//        };
//        probe1.position.addObserver( simpleObserver );
//        probe2.position.addObserver( simpleObserver );
    }

    public static String toCode( ImmutableVector2D v ) {
        return "new ImmutableVector2D(" + v.getX() + "," + v.getY() + ")";
    }

    public void translateBody( Dimension2D dimension2D ) {
        bodyPosition.setValue( bodyPosition.getValue().plus( dimension2D ) );
    }

    /*
     * Moves the sensor body and probes until the hot spot (center of one probe) is on the specified position.
     */
    public void translateToHotSpot( Point2D position ) {
        ImmutableVector2D delta = new ImmutableVector2D( position ).minus( probe1.position.getValue() );
        translateAll( delta );
    }

    public void translateAll( ImmutableVector2D delta ) {
        probe1.translate( delta );
        probe2.translate( delta );
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    public static class Probe {
        public final Property<ImmutableVector2D> position;
        public final Property<Double> value = new Property<Double>( 0.0 );

        public Probe( double x, double y ) {
            position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        }

        public void translate( Dimension2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }

        public void translate( ImmutableVector2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }
    }
}
