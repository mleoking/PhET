// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;

/**
 * Attachment state machine for all transcription factor molecules.  This
 * class controls how transcription factors behave with respect to attaching
 * to and detaching from the DNA molecule, which is the only thing to which the
 * transcription factors attach.
 *
 * @author John Blanco
 */
public class TranscriptionFactorAttachmentStateMachine extends GenericAttachmentStateMachine {

    private static final Random RAND = new Random();

    // Scalar velocity when moving between attachment points on the DNA.
    private static final double VELOCITY_ON_DNA = 200;

    public TranscriptionFactorAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );

        // Set up a new "attached" state, since the behavior is different from
        // the default.
        attachedState = new TranscriptionFactorAttachedState();
    }

    // Subclass of the "attached" state.
    protected class TranscriptionFactorAttachedState extends AttachmentState.GenericAttachedState {
        private static final double DEFAULT_ATTACH_TIME = 0.15; // In seconds.

        private double attachCountdownTime;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == biomolecule;

            // TODO: For now, transcription factors stay attached to a max affinity site forever.  This will probably need to change.
            if ( attachmentSite.getAffinity() < 1 ) {
                // See if we have been attached long enough.
                attachCountdownTime -= dt;
                if ( attachCountdownTime <= 0 ) {
                    List<AttachmentSite> attachmentSites = biomolecule.getModel().getDnaMolecule().getAdjacentAttachmentSites( (TranscriptionFactor) biomolecule, asm.attachmentSite );
                    Collections.shuffle( attachmentSites );
                    // Decide whether to completely detach from the DNA strand or
                    // move to an adjacent attachment point.
                    if ( RAND.nextDouble() > 0.8 || attachmentSites.size() == 0 ) {
                        // Detach.
                        asm.attachmentSite.attachedOrAttachingMolecule.set( null );
                        asm.attachmentSite = null;
                        asm.setState( unattachedButUnavailableState );
                        biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );
                    }
                    else {

                        // Clear the old attachment site.
                        attachmentSite.attachedOrAttachingMolecule.set( null );

                        // Set a new attachment site.
                        attachmentSite = attachmentSites.get( 0 );
                        attachmentSite.attachedOrAttachingMolecule.set( biomolecule );

                        // Set up the state to move to the new attachment site.
                        setState( movingTowardsAttachmentState );
                        biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                    biomolecule.motionBoundsProperty,
                                                                                                    new ImmutableVector2D( 0, 0 ),
                                                                                                    VELOCITY_ON_DNA ) );
                    }
                }
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
            // TODO: For debug, removed eventually.
            if ( asm.attachmentSite.getAffinity() == 1 ) {
                System.out.println( "Transcription factor attached to max affinity site." );
            }
        }
    }
}
