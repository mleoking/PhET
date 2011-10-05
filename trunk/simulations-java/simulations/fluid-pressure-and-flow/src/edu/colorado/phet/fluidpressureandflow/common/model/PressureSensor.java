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

        //When the location or context changes, update the pressure value so readouts will update
        final SimpleObserver updatePressure = new SimpleObserver() {
            public void update() {
                setValue( context.getPressure( location.get().getX(), location.get().getY() ) );
            }
        };
        location.addObserver( updatePressure );
        context.addFluidChangeObserver( updatePressure );
    }

    //Context for pressure sensor, so when the environment changes, the value readout will update
    public static interface Context {
        double getPressure( double x, double y );

        void addFluidChangeObserver( SimpleObserver updatePressure );
    }
}
