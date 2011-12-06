// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;

/**
 * This state controls the behavior of a biomolecule that is moving towards an
 * attachment.  In general, this means that the molecule wanders towards the
 * site and, when it arrives, initiates a state change to the appropriate
 * "attached" state.
 *
 * @author John Blanco
 */
public class MovingTowardsAttachmentState extends BiomoleculeBehaviorState {

    private static final double CAPTURE_DISTANCE = 20; // In picometers, empirically determined.

    private MotionStrategy motionStrategy;
    private final AttachmentSite attachmentSite;

    public MovingTowardsAttachmentState( MobileBiomolecule biomolecule, AttachmentSite attachmentSite ) {
        super( biomolecule );
        this.attachmentSite = attachmentSite;
        motionStrategy = new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty, biomolecule.motionBoundsProperty );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        biomolecule.setPosition( motionStrategy.getNextLocation( biomolecule.getPosition(), biomolecule.getShape(), dt ) );
        if ( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ) < CAPTURE_DISTANCE ) {
            // The molecule has reached the attachment site.  Since the
            // different biomolecules exhibit different behavior once attached,
            // we have the biomolecule generate its own attached state.
            return biomolecule.getAttachmentPointReachedState( attachmentSite );
        }
        else {
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // In this state, the biomolecule is already moving towards an
        // attachment site, so it doesn't consider another.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
        return new UnattachedAndAvailableState( biomolecule );
    }
}
