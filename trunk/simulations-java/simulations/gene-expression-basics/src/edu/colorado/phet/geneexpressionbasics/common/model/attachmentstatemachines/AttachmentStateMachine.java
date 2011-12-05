// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;

/**
 * Base class for the attachment state machines that define how the various
 * biomolecules attach to one another, how they detach from one another, how
 * they find other biomolecules to attach to, and so forth.
 *
 * @author John Blanco
 */
public class AttachmentStateMachine {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Distance within which a molecule is considered to be attached to an
    // attachment site.  This essentially avoids floating point issues.
    private static double ATTACHED_DISTANCE_THRESHOLD = 1; // In picometers.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Reference to the biomolecule controlled by this state machine.
    protected final MobileBiomolecule biomolecule;

    // Attachment point.  When this is non-null, the biomolecule is either
    // attached to this point or moving towards attachment with it.
    protected AttachmentSite attachmentSite = null;

    // States used by this state machine.  These are often set by subclasses
    // to non-default values in order to change the default behavior.
    protected AttachmentState unattachedAndAvailableState = new GenericUnattachedAndAvailableState();
    protected AttachmentState attachedState = new GenericAttachedState();
    protected AttachmentState movingTowardsAttachmentState = new GenericMovingTowardsAttachmentState();
    protected AttachmentState unattachedButUnavailableState = new GenericUnattachedButUnavailableState();

    // Current attachment state.  Changes with each state transition.
    private AttachmentState attachmentState;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public AttachmentStateMachine( MobileBiomolecule biomolecule ) {
        this.biomolecule = biomolecule;
        setState( unattachedAndAvailableState );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        // Step the current state in time.
        attachmentState.stepInTime( this, dt );
    }

    /**
     * Detach the biomolecule from any current attachments.  This will cause
     * the molecule to go into the unattached-bun-unavailable state for some
     * period of time, then it will become available again.
     */
    public void detach() {
        assert attachmentSite != null; // Verify internal state is consistent.
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        attachmentSite = null;
        setState( unattachedButUnavailableState );
    }

    /**
     * Move immediately into the unattached-and-available state.  This is
     * generally done only when the user has grabbed the associated molecule.
     * Calling this when already in this state is harmless.
     */
    public void forceImmediateUnattached() {
        if ( attachmentSite != null ) {
            attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        }
        attachmentSite = null;
        setState( unattachedAndAvailableState );
        biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( biomolecule.motionBoundsProperty ) );
    }

    /**
     * Set a new attachment state.  This calls the "entered" method, so this
     * should be used instead of directly setting the state.
     *
     * @param attachmentState
     */
    protected void setState( AttachmentState attachmentState ) {
        this.attachmentState = attachmentState;
        this.attachmentState.entered( this );
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
    protected abstract class AttachmentState {
        public abstract void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt );

        public abstract void entered( AttachmentStateMachine asm );
    }

    protected class GenericUnattachedAndAvailableState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;

            // Make the biomolecule look for attachments.
            asm.attachmentSite = biomolecule.proposeAttachments();
            if ( asm.attachmentSite != null ) {
                // An attachment proposal was accepted, so start heading towards
                // the attachment site.
                asm.biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                biomolecule.motionBoundsProperty ) );
                asm.setState( asm.movingTowardsAttachmentState );

                // Mark the attachment site as being in use.
                attachmentSite.attachedMolecule.set( new Option.Some<MobileBiomolecule>( biomolecule ) );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // No initialization needed.
        }
    }

    protected class GenericMovingTowardsAttachmentState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get().get() == biomolecule;

            // See if the attachment site has been reached.
            if ( asm.biomolecule.getPosition().distance( asm.attachmentSite.locationProperty.get() ) < ATTACHED_DISTANCE_THRESHOLD ) {
                // This molecule is now at the attachment site, so consider it
                // attached.
                asm.setState( asm.attachedState );
                asm.biomolecule.setMotionStrategy( new FollowAttachmentSite( attachmentSite ) );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // No initialization needed.
        }
    }

    // The generic "attached" state isn't very useful, but is included for
    // completeness.  The reason it isn't useful is because the different
    // biomolecules all have their own unique behavior with respect to
    // attaching, and thus define their own attached states.
    protected class GenericAttachedState extends AttachmentState {

        private static final double DEFAULT_ATTACH_TIME = 3; // In seconds.

        private double attachCountdownTime = DEFAULT_ATTACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get().get() == biomolecule;

            // See if it is time to detach.
            attachCountdownTime -= dt;
            if ( attachCountdownTime <= 0 ) {
                // Detach.
                asm.attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
                asm.attachmentSite = null;
                asm.setState( unattachedButUnavailableState );
                biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
        }
    }

    protected class GenericUnattachedButUnavailableState extends AttachmentState {

        private static final double DEFAULT_DETACH_TIME = 3; // In seconds.

        private double detachCountdownTime = DEFAULT_DETACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;

            // See if we've been detached long enough.
            detachCountdownTime -= dt;
            if ( detachCountdownTime <= 0 ) {
                // Move the the unattached-and-available state.
                asm.setState( asm.unattachedButUnavailableState );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            detachCountdownTime = DEFAULT_DETACH_TIME;
        }
    }
}
