// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Model component for the VelocitySensorNode, which has a position (for the hot-spot of the sensor) and a numerical readout.
 *
 * @author Sam Reid
 */
public class VelocitySensor extends PointSensor<ImmutableVector2D> {

    public VelocitySensor() {
        this( 0, 0 );
    }

    public VelocitySensor( double x, double y ) {
        super( x, y );
    }
}