// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model instance that can determine the pressure at its location.
 *
 * @author Sam Reid
 */
public class PressureSensor extends Sensor<Double> {

    public PressureSensor( final Context context, double x, double y ) {
        super( x, y, context.getPressure( x, y ) );
        final SimpleObserver updatePressure = new SimpleObserver() {
            public void update() {
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

        void addFluidChangeObserver( SimpleObserver updatePressure );
    }
}
