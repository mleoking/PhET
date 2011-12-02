// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;

/**
 * Base class for the attachment state machines that define how the various
 * biomolecules attach to one another, how they detach from one another, how
 * they find other biomolecules to attach to, and so forth.
 *
 * @author John Blanco
 */
public abstract class AttachmentStateMachine {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Reference to the biomolecule controlled by this state machine.
    protected final MobileBiomolecule biomolecule;

    // Current attachment state.
    private AttachmentState attachmentState;

    // Attachment point.  When this is non-null, the biomolecule is either
    // attached to this point or moving towards attachment with it.
    protected AttachmentSite attachmentSite = null;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected AttachmentStateMachine( MobileBiomolecule biomolecule ) {
        this.biomolecule = biomolecule;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        attachmentState.stepInTime( this, dt );
    }

    /**
     * Detach the biomolecule from any current attachments.  This will cause
     * the molecule to go into the unattached-bun-unavailable state for some
     * period of time, then it will become available again.
     */
    protected void detach() {
        assert attachmentSite != null; // Verify internal state is consistent.
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        attachmentSite = null;
        attachmentState = new GenericUnattachedButUnavailableState();
    }

    /**
     * Move immediately into the unattached-and-available state.  This is
     * generally done only when the user has grabbed the associated molecule.
     */
    public void forceImmediateUnattached() {
        assert attachmentSite != null; // Verify internal state is consistent.
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        attachmentSite = null;
        attachmentState = new GenericUnattachedButUnavailableState();
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    public enum AttachmentStateEnum {
        UNATTACHED_AND_AVAILABLE,
        MOVING_TOWARDS_ATTACHMENT,
        ATTACHED,
        UNATTACHED_BUT_UNAVAILABLE
    }

    /**
     * Base class for individual attachment states, used by the enclosing
     * state machine.
     */
    private abstract class AttachmentState {
        public abstract void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt );
    }

    private class GenericUnattachedAndAvailableState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;
            assert asm.attachmentSite.attachedMolecule.get() == new Option.Some<MobileBiomolecule>( biomolecule );

            // Make the biomolecule look for attachments.
            asm.attachmentSite = biomolecule.proposeAttachments();
            if ( asm.attachmentSite != null ) {
                // An attachment proposal was accepted, so start heading towards
                // the attachment site.
                asm.biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                biomolecule.motionBoundsProperty ) );
                asm.attachmentState = new GenericMovingTowardsAttachmentState();
            }
        }
    }

    private class GenericMovingTowardsAttachmentState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get() == new Option.Some<MobileBiomolecule>( asm.biomolecule );

            // See of the attachment site has been reached.
            if ( asm.biomolecule.getPosition().equals( asm.attachmentSite.locationProperty.get() ) ) {
                // This molecule is now at the attachment site, so consider it
                // attached.
                attachmentState = new GenericAttachedState();
                asm.biomolecule.setMotionStrategy( new FollowAttachmentSite( attachmentSite ) );
            }
        }
    }

    // The generic "attached" state isn't very useful, but is included for
    // completeness.  The reason it isn't useful is because the different
    // biomolecules all have their own unique behavior with respect to
    // attaching, and thus define their own attached states.
    private class GenericAttachedState extends AttachmentState {

        private static final double DEFAULT_ATTACH_TIME = 3; // In seconds.

        private double attachCountdownTime = DEFAULT_ATTACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get() == new Option.Some<MobileBiomolecule>( biomolecule );

            // See if it is time to detach.
            attachCountdownTime -= dt;
            if ( attachCountdownTime <= attachCountdownTime ) {
                // Detach.
                asm.attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
                asm.attachmentState = new GenericUnattachedButUnavailableState();
            }
        }
    }


    private class GenericUnattachedButUnavailableState extends AttachmentState {

        private static final double DEFAULT_DETACH_TIME = 3; // In seconds.

        private double detachCountdownTime = DEFAULT_DETACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get() == new Option.Some<MobileBiomolecule>( biomolecule );

            // See if we've been detached long enough.
            detachCountdownTime -= DEFAULT_DETACH_TIME;
            if ( detachCountdownTime <= 0 ) {
                // Move the the unattached-and-available state.
                asm.attachmentState = new GenericUnattachedAndAvailableState();
            }
        }
    }
}
