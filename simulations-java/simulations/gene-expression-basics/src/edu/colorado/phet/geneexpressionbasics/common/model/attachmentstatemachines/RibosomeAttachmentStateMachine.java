// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;

/**
 * This class defined the attachment state machine for all ribosomes.
 * Ribosomes pretty much only connect to mRNA, so that's what this controls.
 *
 * @author John Blanco
 */
public class RibosomeAttachmentStateMachine extends AttachmentStateMachine {

    // Scalar velocity for transcription.
    private static final double RNA_TRANSCRIPTION_RATE = 100; // Picometers per second.

    // Reference back to the ribosome that is controlled by this state machine.
    private final Ribosome ribosome;

    public RibosomeAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );

        // Set up a local reference of the needed type.
        ribosome = (Ribosome) biomolecule;

        // Set up a non-default "attached" state, since the behavior is
        // different from the default.
        attachedState = new RibosomeAttachedState();
    }

    /**
     * Class that defines what the ribosome does when attached to mRNA, which
     * is essentially to transcribe it.
     */
    protected class RibosomeAttachedState extends GenericAttachedState {

        double tempCounter;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            tempCounter -= dt;
            if ( tempCounter <= 0 ) {
                ribosome.releaseMessengerRna();
                asm.detach();
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            tempCounter = 2; // Seconds.
        }
    }
}
