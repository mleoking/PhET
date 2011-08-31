// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.fluidpressureandflow.common.model.VelocitySensorContext;

/**
 * Velocity sensor customized for Fluid Pressure and Flow sim
 *
 * @author Sam Reid
 */
public class FPAFVelocitySensor extends VelocitySensor {

    public final SimpleObserver updateValue;

    public FPAFVelocitySensor( final VelocitySensorContext context, double x, double y ) {
        super( x, y );
        updateValue = new SimpleObserver() {
            public void update() {
                value.set( new Option.Some<ImmutableVector2D>( context.getVelocity( position.get().getX(), position.get().getY() ) ) );
            }
        };
        position.addObserver( updateValue );
        context.addVelocityUpdateListener( updateValue );
    }

    @Override public void reset() {
        super.reset();

        //Update the value in the sensor, without this call it would reset to "?" even if 0.00 was the original value
        updateValue.update();
    }
}
