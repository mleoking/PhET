// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * State where the biomolecule is not attached but is also available to attach
 * to anything.  This exists to create some hysteresis between attachments.
 *
 * @author John Blanco
 */
public class UnattachedButUnavailableState extends BiomoleculeBehaviorState {
    private static final double UNAVAILABLE_TIME = 3; // In seconds.

    private final IMotionStrategy motionStrategy = new RandomWalkMotionStrategy();

    private double unavailableCountdownTime = UNAVAILABLE_TIME;

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );
        unavailableCountdownTime -= dt;
        if ( unavailableCountdownTime <= 0 ) {
            // Change state to become available for attaching.
            return new UnattachedAndAvailableState();
        }
        else {
            // No state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule ) {
        // Unavailable, so reject request by leaving the attachment site
        // unchanged and returning the current state.
        return this;
    }
}
