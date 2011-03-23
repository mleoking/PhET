// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Model component for the VelocitySensorNode, which has a position (for the hot-spot of the sensor) and a numerical readout.
 *
 * @author Sam Reid
 */
public class VelocitySensor {
    public final Property<ImmutableVector2D> position;
    public final Property<Option<ImmutableVector2D>> value = new Property<Option<ImmutableVector2D>>( new Option.None<ImmutableVector2D>() );

    public VelocitySensor() {
        this( 0, 0 );
    }

    public VelocitySensor( double x, double y ) {
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    public void translate( Dimension2D delta ) {
        position.setValue( position.getValue().plus( delta.getWidth(), delta.getHeight() ) );
    }

    public void reset() {
        position.reset();
        value.reset();
    }
}
