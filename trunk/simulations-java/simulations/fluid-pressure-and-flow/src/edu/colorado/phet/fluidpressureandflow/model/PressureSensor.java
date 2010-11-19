package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class PressureSensor extends Sensor {
    private Property<Double> pressureProperty = new Property<Double>( 0.0 );

    //TODO: redesign so that this takes a Function<Point2D,Double> or equivalent (must be listenable)

    public PressureSensor( final FluidPressureAndFlowModel context, double x, double y ) {
        super( x, y );
        final SimpleObserver updatePressure = new SimpleObserver() {
            public void update() {
                pressureProperty.setValue( context.getPressure( getLocation().toPoint2D() ) );
            }
        };
        addPositionObserver( updatePressure );
        context.addFluidChangeObserver( updatePressure );
    }

    public double getPressure() {
        return pressureProperty.getValue();
    }

    public void addPressureObserver( SimpleObserver simpleObserver ) {
        pressureProperty.addObserver( simpleObserver );
    }
}
