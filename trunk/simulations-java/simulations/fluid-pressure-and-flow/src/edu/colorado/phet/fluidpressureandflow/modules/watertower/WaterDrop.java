// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WaterDrop {
    private Property<ImmutableVector2D> location;
    private Property<ImmutableVector2D> velocity;
    private final double mass = 1;
    private final double gravity = -9.8;
    private ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();

    public WaterDrop( ImmutableVector2D location, ImmutableVector2D velocity ) {
        this.location = new Property<ImmutableVector2D>( location );
        this.velocity = new Property<ImmutableVector2D>( velocity );
    }

    public void stepInTime( double simulationTimeChange ) {
        ImmutableVector2D force = new ImmutableVector2D( 0, mass * gravity );
        ImmutableVector2D acceleration = force.getScaledInstance( 1.0 / mass );
        velocity.setValue( acceleration.getScaledInstance( simulationTimeChange ).getAddedInstance( velocity.getValue() ) );
        location.setValue( velocity.getValue().getScaledInstance( simulationTimeChange ).getAddedInstance( location.getValue() ) );
    }

    public Property<ImmutableVector2D> getPositionProperty() {
        return location;
    }

    public void addRemovalListener( SimpleObserver simpleObserver ) {
        removalListeners.add( simpleObserver );
    }
}
