// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;

/**
 * Generic attachment state machine - just implements basic behavior.
 * <p/>
 * This class exists mainly for testing and for quick implementation of
 * biomolecules.  The code analyzer may show that it is unused, but it should
 * be kept around anyway for testing and prototyping of changes.
 *
 * @author John Blanco
 */
public class GenericAttachmentStateMachine extends AttachmentStateMachine {

    // States used by this state machine.  These are often set by subclasses
    // to non-default values in order to change the default behavior.
    protected AttachmentState unattachedAndAvailableState = new AttachmentState.GenericUnattachedAndAvailableState();
    protected AttachmentState attachedState = new AttachmentState.GenericAttachedState();
    protected AttachmentState movingTowardsAttachmentState = new AttachmentState.GenericMovingTowardsAttachmentState();
    protected AttachmentState unattachedButUnavailableState = new AttachmentState.GenericUnattachedButUnavailableState();

    public GenericAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        setState( unattachedAndAvailableState );
    }

    @Override public void detach() {
        assert attachmentSite != null; // Verify internal state is consistent.
        attachmentSite.attachedOrAttachingMolecule.set( null );
        attachmentSite = null;
        forceImmediateUnattachedButUnavailable();
    }

    @Override public void forceImmediateUnattachedAndAvailable() {
        if ( attachmentSite != null ) {
            attachmentSite.attachedOrAttachingMolecule.set( null );
        }
        attachmentSite = null;
        setState( unattachedAndAvailableState );
    }

    @Override public void forceImmediateUnattachedButUnavailable() {
        if ( attachmentSite != null ) {
            attachmentSite.attachedOrAttachingMolecule.set( null );
        }
        attachmentSite = null;
        biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( biomolecule.getDetachDirection(),
                                                                                   biomolecule.motionBoundsProperty ) );
        setState( unattachedButUnavailableState );
    }
}
