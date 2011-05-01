// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property2.Observer;
import edu.colorado.phet.common.phetcommon.model.property2.UpdateEvent;

/**
 * Model instance that can determine the pressure at its location.
 *
 * @author Sam Reid
 */
public class PressureSensor extends Sensor<Double> {

    public PressureSensor( final Context context, double x, double y ) {
        super( x, y, context.getPressure( x, y ) );
        Observer<ImmutableVector2D> updatePressure = new Observer<ImmutableVector2D>() {
            @Override public void update( UpdateEvent<ImmutableVector2D> immutableVector2DUpdateEvent ) {
                setValue( context.getPressure( getLocation().getX(), getLocation().getY() ) );
            }
        };
        location.addObserver( updatePressure );
        context.addFluidChangeObserver( updatePressure );
    }

    @Override
    public double getScalarValue() {
        return getValue();
    }

    public static interface Context {
        double getPressure( double x, double y );

        void addFluidChangeObserver( Observer<ImmutableVector2D> updatePressure );
    }
}
