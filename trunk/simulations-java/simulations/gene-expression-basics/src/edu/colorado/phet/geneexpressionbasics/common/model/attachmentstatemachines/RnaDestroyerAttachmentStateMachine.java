// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.DestroyerTrackingRnaMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRnaDestroyer;

/**
 * This class defined the attachment state machine for the biomolecules that
 * destroy the messenger RNA molecules.
 *
 * @author John Blanco
 */
public class RnaDestroyerAttachmentStateMachine extends GenericAttachmentStateMachine {

    // Scalar velocity for transcription.
    private static final double RNA_DESTRUCTION_RATE = 25; // Picometers per second.

    // Reference back to the mRNA destroyer that is controlled by this state machine.
    private final MessengerRnaDestroyer mRnaDestroyer;

    public RnaDestroyerAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );

        // Set up a local reference of the needed type.
        mRnaDestroyer = (MessengerRnaDestroyer) biomolecule;

        // Set up a non-default "attached" state, since the behavior is
        // different from the default.
        attachedState = new MRnaDestroyerAttachedState();
    }

    /**
     * Class that defines what the mRNA destroyer does when attached to mRNA.
     */
    protected class MRnaDestroyerAttachedState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            // TODO: Create a fragment of mRNA.

            // Advance the destruction of the mRNA.
            boolean destructionComplete = mRnaDestroyer.advanceMessengerRnaDestruction( RNA_DESTRUCTION_RATE );
            if ( destructionComplete ) {
                // Release this destroyer to wander in the cytoplasm.
                asm.detach();
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            mRnaDestroyer.initiateMessengerRnaDestruction();
            mRnaDestroyer.setMotionStrategy( new DestroyerTrackingRnaMotionStrategy( mRnaDestroyer ) );
        }
    }
}
