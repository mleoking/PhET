// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites, final MobileBiomolecule biomolecule ) {
        // Since this state is unattached, we choose the most appealing
        // proposal and immediately accept it by transitioning to the attached
        // state and marking the attachment site as being in use by this
        // molecule.
        if ( proposedAttachmentSites.size() > 0 ) {
            // Sort the list based on a combination of the proximity and
            // affinity of each site.
            Collections.sort( proposedAttachmentSites, new Comparator<AttachmentSite>() {
                public int compare( AttachmentSite o1, AttachmentSite o2 ) {
                    return Double.compare( Math.pow( biomolecule.getPosition().distance( o1.locationProperty.get() ), 2 ) * o1.getAffinity(),
                                           Math.pow( biomolecule.getPosition().distance( o2.locationProperty.get() ), 2 ) * o2.getAffinity() );
                }
            } );
            // Use the affinity off the top of the list.
            proposedAttachmentSites.get( 0 ).inUse.set( true );
            return new MovingTowardsAttachmentState( proposedAttachmentSites.get( 0 ) );
        }
        else {
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        // Since this molecule was already unattached and available, no
        // change is needed.
        return this;
    }
}
