// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.StillnessMotionStrategy;

/**
 * Attachment state machine for all protein molecules.  This class controls
 * how protein molecules behave with respect to attachments.
 *
 * @author John Blanco
 */
public class ProteinAttachmentStateMachine extends GenericAttachmentStateMachine {

    public ProteinAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        setState( new ProteinAttachedToRibosomeState() );

        // Set up a new "attached" state, since the behavior is different from
        // the default.
        attachedState = new ProteinAttachedToRibosomeState();
    }

    // Subclass of the "attached" state.
    protected class ProteinAttachedToRibosomeState extends AttachmentState {

        @Override public void entered( AttachmentStateMachine asm ) {
            biomolecule.setMotionStrategy( new StillnessMotionStrategy() );
            // Prevent user interaction while the protein is growing.
            asm.biomolecule.movableByUser.set( false );
        }
    }
}
