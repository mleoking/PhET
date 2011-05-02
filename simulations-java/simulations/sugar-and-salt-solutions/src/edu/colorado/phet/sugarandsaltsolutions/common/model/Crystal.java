// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property2.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * A single solid crystal (sugar or salt) that comes from a shaker and gets dissolved in the water.
 *
 * @author Sam Reid
 */
public class Crystal {
    public final double mass = 1E-6;//kg
    public Property<ImmutableVector2D> position;
    public Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public Property<ImmutableVector2D> acceleration = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();

    public Crystal( ImmutableVector2D position ) {
        this.position = new Property<ImmutableVector2D>( position );
    }

    //propagate the crystal according to the specified applied forces, using euler integration
    public void stepInTime( ImmutableVector2D appliedForce, double dt ) {
        acceleration.setValue( appliedForce.times( 1.0 / mass ) );
        velocity.setValue( velocity.getValue().plus( acceleration.getValue().times( dt ) ) );
        position.setValue( position.getValue().plus( velocity.getValue().times( dt ) ) );
    }

    //Add a listener which will be notified when this crystal is removed from the model
    public void addRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.add( removalListener );
    }

    //Remove a removal listener
    public void removeRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.remove( removalListener );
    }

    //Notify all removal listeners that this crystal is being removed from the model
    public void remove() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
    }
}
