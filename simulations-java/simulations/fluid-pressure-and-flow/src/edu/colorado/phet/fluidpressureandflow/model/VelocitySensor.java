package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class VelocitySensor extends Sensor {

    private final Property<ImmutableVector2D> velocityProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );

    public VelocitySensor( final Context context, final double x, final double y ) {
        super( x, y );
        final SimpleObserver updateVelocity = new SimpleObserver() {
            public void update() {
                setVelocity( context.getVelocity( getX(), getY() ) );
            }
        };
        addLocationObserver( updateVelocity );
        context.addVelocityUpdateListener( updateVelocity ); //pipe could change underneath the velocity sensor
    }
    
    public void reset() {
        super.reset();
        velocityProperty.reset();
    }
    
    public ImmutableVector2D getVelocity() {
        return velocityProperty.getValue();
    }

    private void setVelocity( ImmutableVector2D value ) {
        velocityProperty.setValue( value );
    }

    public void addVelocityObserver( SimpleObserver observer ) {
        velocityProperty.addObserver( observer );
    }
    
    public static interface Context {
        public ImmutableVector2D getVelocity( double x, double y );

        public void addVelocityUpdateListener( SimpleObserver observer );
    }
}
