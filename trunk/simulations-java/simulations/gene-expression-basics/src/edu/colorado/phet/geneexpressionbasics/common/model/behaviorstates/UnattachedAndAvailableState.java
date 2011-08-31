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

    private final MotionStrategy motionStrategy;

    public UnattachedAndAvailableState( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        motionStrategy = new RandomWalkMotionStrategy( biomolecule.getMotionBoundsProperty() );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Exhibit random walk motion.
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );

        // This particular state never changes as a result of time dependent
        // behavior, so it always returns itself.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Since this state is unattached, we choose the most appealing
        // proposal and immediately accept it by transitioning to the "moving
        // towards attachment" state and marking the attachment site as being
        // in use by this molecule.
        if ( proposedAttachmentSites.size() > 0 ) {
            List<AttachmentSite> copyOfProposedAttachmentSites = new ArrayList<AttachmentSite>( proposedAttachmentSites );
            double maxAttraction = 0;
            for ( AttachmentSite proposedAttachmentSite : copyOfProposedAttachmentSites ) {
                maxAttraction = Math.max( getAttraction( biomolecule, proposedAttachmentSite ), maxAttraction );
            }
            for ( AttachmentSite proposedAttachmentSite : proposedAttachmentSites ) {
                if ( getAttraction( biomolecule, proposedAttachmentSite ) < maxAttraction ) {
                    // This attachment site has less of an attraction
                    // than at least one of the others, so remove it
                    // from consideration.
                    copyOfProposedAttachmentSites.remove( proposedAttachmentSite );
                }
            }
            // Choose randomly between all the equivalent attachment sites.
            AttachmentSite newAttachmentSite = copyOfProposedAttachmentSites.get( RAND.nextInt( copyOfProposedAttachmentSites.size() ) );
            // Accept the new attachment site.
            newAttachmentSite.attachedMolecule.set( new Option.Some<MobileBiomolecule>( biomolecule ) );
            return new MovingTowardsAttachmentState( biomolecule, newAttachmentSite );
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
