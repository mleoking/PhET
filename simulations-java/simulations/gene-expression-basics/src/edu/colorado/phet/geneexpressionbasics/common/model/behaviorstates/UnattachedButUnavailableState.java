// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;

/**
 * State where the biomolecule is not attached but is also available to attach
 * to anything.  This exists to create some hysteresis between attachments.
 *
 * @author John Blanco
 */
public class UnattachedButUnavailableState extends BiomoleculeBehaviorState {

    private static final double UNAVAILABLE_TIME = 3; // In seconds.

    private final MotionStrategy motionStrategy;

    private double unavailableCountdownTime = UNAVAILABLE_TIME;

    protected UnattachedButUnavailableState( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        motionStrategy = new RandomWalkMotionStrategy( biomolecule.getMotionBounds() );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );
        unavailableCountdownTime -= dt;
        if ( unavailableCountdownTime <= 0 ) {
            // Change state to become available for attaching.
            return new UnattachedAndAvailableState( biomolecule );
        }
        else {
            // No state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Unavailable, so reject request by leaving the attachment site
        // unchanged and returning the current state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        // Become immediately available for attaching again.
        unavailableCountdownTime = 0;
        return new UnattachedAndAvailableState( biomolecule );
    }
}
