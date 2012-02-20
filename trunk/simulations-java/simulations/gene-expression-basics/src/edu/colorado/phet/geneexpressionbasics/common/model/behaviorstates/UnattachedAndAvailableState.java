// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;

/**
 * @author John Blanco
 */
public class UnattachedAndAvailableState extends BiomoleculeBehaviorState {

    // Distance beyond which no attachments are considered.
    private static final double ATTACH_DISTANCE_THRESHOLD = 500; // picometers

    private final MotionStrategy motionStrategy;

    public UnattachedAndAvailableState( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        motionStrategy = new RandomWalkMotionStrategy( biomolecule.motionBoundsProperty );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Exhibit random walk motion.
        biomolecule.setPosition( motionStrategy.getNextLocation( biomolecule.getPosition(), biomolecule.getShape(), dt ) );

        // This particular state never changes as a result of time dependent
        // behavior, so it always returns itself.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Look at the list of potential attachment sites and decide if any are
        // close enough and strong enough to consider.
        if ( proposedAttachmentSites.size() > 0 ) {
            List<AttachmentSite> attachmentSitesToConsider = new ArrayList<AttachmentSite>( proposedAttachmentSites );
            // Eliminate sites that are too far away.
            for ( AttachmentSite proposedAttachmentSite : proposedAttachmentSites ) {
                if ( proposedAttachmentSite.locationProperty.get().distance( biomolecule.getPosition() ) > ATTACH_DISTANCE_THRESHOLD ) {
                    attachmentSitesToConsider.remove( proposedAttachmentSite );
                }
            }
            if ( attachmentSitesToConsider.isEmpty() ) {
                // No attachment sites in range, so no state change.
                return this;
            }

            // Determine the maximum attraction of the in-range attachment sites.
            double maxAttraction = 0;
            for ( AttachmentSite proposedAttachmentSite : attachmentSitesToConsider ) {
                maxAttraction = Math.max( getAttraction( biomolecule, proposedAttachmentSite ), maxAttraction );
            }
            List<AttachmentSite> copyOfAttachmentSiteList = new ArrayList<AttachmentSite>( attachmentSitesToConsider );
            for ( AttachmentSite proposedAttachmentSite : copyOfAttachmentSiteList ) {
                if ( getAttraction( biomolecule, proposedAttachmentSite ) < maxAttraction ) {
                    // This attachment site has less of an attraction
                    // than at least one of the others, so remove it
                    // from consideration.
                    attachmentSitesToConsider.remove( proposedAttachmentSite );
                }
            }

            // Choose randomly between all the equivalent attachment sites.
            AttachmentSite newAttachmentSite = attachmentSitesToConsider.get( RAND.nextInt( attachmentSitesToConsider.size() ) );
            // Accept the new attachment site.
            newAttachmentSite.attachedOrAttachingMolecule.set(biomolecule );
            return new MovingTowardsAttachmentState( biomolecule, newAttachmentSite );
        }
        else {
            // No sites on list, so no state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        // Since this molecule was already unattached and available, no
        // change is needed.
        return this;
    }
}
