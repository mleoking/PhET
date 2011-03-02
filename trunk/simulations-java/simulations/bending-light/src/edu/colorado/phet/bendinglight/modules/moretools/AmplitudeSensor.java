// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class AmplitudeSensor {
    public final Probe probe1 = new Probe();
    public final Probe probe2 = new Probe();
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );

    public void translateBody( Dimension2D dimension2D ) {
        bodyPosition.setValue( bodyPosition.getValue().plus( dimension2D ) );
    }

    public static class Probe {
        public final Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        public final Property<Double> value = new Property<Double>( 0.0 );

        public void translate( Dimension2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }
    }
}
