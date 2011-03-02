// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class VelocitySensor {
    public final Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public final Property<Double> value = new Property<Double>( 0.0 );

    public void translate( Dimension2D delta ) {
        position.setValue( position.getValue().getAddedInstance( delta.getWidth(), delta.getHeight() ) );
    }
}
