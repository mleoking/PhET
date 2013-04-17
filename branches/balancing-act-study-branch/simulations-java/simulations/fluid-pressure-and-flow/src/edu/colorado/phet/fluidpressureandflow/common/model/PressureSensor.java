// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model instance that can determine the pressure at its location.
 *
 * @author Sam Reid
 */
public class PressureSensor extends Sensor<Double> {

    public final Context context;

    public PressureSensor( IUserComponent userComponent, final Context context, double x, double y ) {
        super( x, y, context.getPressure( x, y ), userComponent );

        this.context = context;

        //When the location or context changes, update the pressure value so readouts will update
        final SimpleObserver updatePressure = new SimpleObserver() {
            public void update() {
                setValue( context.getPressure( location.get().getX(), location.get().getY() ) );
            }
        };
        location.addObserver( updatePressure );
        context.addPressureChangeObserver( updatePressure );
    }

    //Context for pressure sensor, so when the environment changes, the value readout will update
    public static interface Context {
        Option<Double> getPressure( double x, double y );

        void addPressureChangeObserver( SimpleObserver updatePressure );

        boolean isInWaterTowerWater( double x, double y );
    }
}
