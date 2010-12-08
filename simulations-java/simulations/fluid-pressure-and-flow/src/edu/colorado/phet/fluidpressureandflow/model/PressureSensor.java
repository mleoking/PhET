package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class PressureSensor extends Sensor<Double> {

    //TODO: redesign so that this takes a Function<Point2D,Double> or equivalent (must be listenable)

    public PressureSensor( final FluidPressureAndFlowModel context, double x, double y ) {
        super( x, y, context.getPressure( x, y ) );
        final SimpleObserver updatePressure = new SimpleObserver() {
            public void update() {
                setValue( context.getPressure( getLocation().toPoint2D() ) );
            }
        };
        addLocationObserver( updatePressure );
        context.addFluidChangeObserver( updatePressure );
    }

    @Override
    public double getScalarValue() {
        return getValue();
    }
}
