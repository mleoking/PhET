// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * @author John Blanco
 */
public class UnattachedAndAvailableState extends BiomoleculeBehaviorState {

    private final IMotionStrategy motionStrategy = new RandomWalkMotionStrategy();

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        // Exhibit random walk motion.
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );

        // This particular state never changes as a result of time dependent
        // behavior, so it always returns itself.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule ) {
        // Since this state is unattached, we immediately accept the proposal by
        // transitioning to the attached state and marking the attachment site
        // as being in use by this molecule.
        attachmentSite.inUse.set( true );
        return new MovingTowardsAttachmentState( attachmentSite );
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        // Since this molecule was already unattached and available, no
        // change is needed.
        return this;
    }
}
