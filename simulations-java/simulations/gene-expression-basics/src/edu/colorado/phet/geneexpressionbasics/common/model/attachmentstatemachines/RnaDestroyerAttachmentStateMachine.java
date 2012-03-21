// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.DestroyerTrackingRnaMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRnaDestroyer;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRnaFragment;

/**
 * This class defined the attachment state machine for the biomolecules that
 * destroy the messenger RNA molecules.
 *
 * @author John Blanco
 */
public class RnaDestroyerAttachmentStateMachine extends GenericAttachmentStateMachine {

    // Scalar velocity for transcription.
    private static final double RNA_DESTRUCTION_RATE = 750; // Picometers per second.

    // Range of lengths for mRNA fragments.
    private static DoubleRange MRNA_FRAGMENT_LENGTH_RANGE = new DoubleRange( 100, 400 ); // In picometers.

    private static final Random RAND = new Random();

    // Reference back to the mRNA destroyer that is controlled by this state machine.
    protected final MessengerRnaDestroyer mRnaDestroyer;

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

        private MessengerRnaFragment messengerRnaFragment = null;
        private double targetFragmentLength = 0;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == asm.biomolecule;

            // Grow the mRNA fragment, release it if it is time to do so.
            if ( messengerRnaFragment == null ) {
                messengerRnaFragment = new MessengerRnaFragment( biomolecule.getModel(), biomolecule.getPosition() );
                biomolecule.getModel().addMobileBiomolecule( messengerRnaFragment );
                targetFragmentLength = MRNA_FRAGMENT_LENGTH_RANGE.getMin() + RAND.nextDouble() * MRNA_FRAGMENT_LENGTH_RANGE.getLength();
            }
            messengerRnaFragment.addLength( RNA_DESTRUCTION_RATE * dt );
            if ( messengerRnaFragment.getLength() >= targetFragmentLength ) {
                // Time to release this fragment.
                messengerRnaFragment.releaseFromDestroyer();
                messengerRnaFragment = null;
            }

            // Advance the destruction of the mRNA.
            boolean destructionComplete = mRnaDestroyer.advanceMessengerRnaDestruction( RNA_DESTRUCTION_RATE * dt );
            if ( destructionComplete ) {
                // Detach the current mRNA fragment.
                if ( messengerRnaFragment != null ) {
                    messengerRnaFragment.releaseFromDestroyer();
                    messengerRnaFragment = null;
                }

                // Remove the messenger RNA that is now destroyed from the
                // model.  There should be no visual representation left to it
                // at this point.
                biomolecule.getModel().removeMessengerRna( mRnaDestroyer.getMessengerRnaBeingDestroyed() );
                mRnaDestroyer.clearMessengerRnaBeingDestroyed();

                // Release this destroyer to wander in the cytoplasm.
                asm.detach();
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            mRnaDestroyer.initiateMessengerRnaDestruction();
            mRnaDestroyer.setMotionStrategy( new DestroyerTrackingRnaMotionStrategy( mRnaDestroyer ) );
            // Turn off user interaction while mRNA is being destroyed.
            asm.biomolecule.movableByUser.set( false );
        }
    }
}
