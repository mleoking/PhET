// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * @author John Blanco
 */
public class MovingTowardsAttachmentState extends BiomoleculeBehaviorState {

    private static final double CAPTURE_DISTANCE = 20; // In picometers.

    private IMotionStrategy motionStrategy;
    private final AttachmentSite attachmentSite;

    public MovingTowardsAttachmentState( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
        motionStrategy = new MeanderToDestinationMotionStrategy( attachmentSite.locationProperty );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );
        if ( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ) < CAPTURE_DISTANCE ) {
            // The molecule has reached the attachment site.  Time to
            // transition to the Attached state.
            return new AttachedState( attachmentSite );
        }
        else {
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule ) {
        // In this state, the biomolecule is already moving towards an
        // attachment site, so it doesn't consider another.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.inUse.set( false );
        return new UnattachedAndAvailableState();
    }
}
