// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.model;

import java.util.ArrayList;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a
 * teeter totter.
 *
 * @author John Blanco
 */
public class BalanceLabModel extends BalanceModel {

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public BalanceLabModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clock.getDt() );
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    //REVIEW return values is never used, and it's odd that this returns the same thing it was passed but with a less-specific type. Recommend returning void.
    @Override public UserMovableModelElement addMass( final Mass mass ) {
        //REVIEW this update method is fired on registration, so shouldn't you add the mass to the list before adding the observer?
        mass.userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isUserControlled, Boolean wasUserControlled ) {
                if ( !isUserControlled && wasUserControlled ) {
                    // The user has dropped this mass.
                    if ( !plank.addMassToSurface( mass ) ) {
                        // The attempt to add mass to surface of plank failed,
                        // probably because the area below the mass is full,
                        // or because the mass wasn't over the plank.
                        removeMassAnimated( mass );
                    }
                }
            }
        } );
        massList.add( mass );
        return mass;
    }

    @Override public void reset() {
        super.reset();
        // Remove this model's references to the masses.
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            removeMass( mass );
        }
    }

    /**
     * Remove the mass from the model, but move it and scale it as it moves so
     * that it looks like it returns to the tool box.
     *
     * @param mass
     */
    protected void removeMassAnimated( final Mass mass ) {
        // Register a listener for the completion of the removal animation sequence.
        mass.addAnimationStateObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isAnimating, Boolean wasAnimating ) {
                if ( wasAnimating && !isAnimating ) {
                    // Animation sequence has completed.
                    mass.removeAnimationStateObserver( this );
                    massList.remove( mass );
                }
            }
        } );
        // Kick off the animation back to the tool box.
        mass.initiateAnimation();
    }
}