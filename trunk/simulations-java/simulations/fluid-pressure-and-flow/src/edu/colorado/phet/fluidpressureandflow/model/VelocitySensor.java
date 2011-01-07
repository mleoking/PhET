// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class VelocitySensor extends Sensor<ImmutableVector2D> {

    public VelocitySensor( final Context context, final double x, final double y ) {
        super( x, y, context.getVelocity( x, y ) );
        final SimpleObserver updateVelocity = new SimpleObserver() {
            public void update() {
                setValue( context.getVelocity( getX(), getY() ) );
            }
        };
        addLocationObserver( updateVelocity );
        context.addVelocityUpdateListener( updateVelocity ); //pipe could change underneath the velocity sensor
    }

    @Override
    public double getScalarValue() {
        return getValue().getMagnitude();
    }

    public static interface Context {
        public ImmutableVector2D getVelocity( double x, double y );

        public void addVelocityUpdateListener( SimpleObserver observer );
    }
}
