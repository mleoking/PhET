package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.modules.fluidflow.FluidFlowModel;

/**
 * @author Sam Reid
 */
public class VelocitySensor extends Sensor {
    private final FluidFlowModel context;
    private final Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D() );

    public VelocitySensor( final double x, final double y, final FluidFlowModel context ) {
        super( x, y );
        this.context = context;
        addPositionObserver( new SimpleObserver() {
            public void update() {
                velocity.setValue( context.getVelocity( getX(), getY() ) );
            }
        } );
    }

    public void addVelocityObserver( SimpleObserver observer ) {
        velocity.addObserver( observer );
    }

    public Property<ImmutableVector2D> getVelocity() {
        return velocity;
    }
}
