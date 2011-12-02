// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;

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

    private static final double DEFAULT_DETACH_TIME = 3; // In seconds.

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

    // Countdown timer used when detaching from an attachment site.
    private double detachCountdownTimer = DEFAULT_DETACH_TIME;

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
        switch( attachmentState ) {
            case UNATTACHED_AND_AVAILABLE:
                stepUnattachedAndAvailableStateInTime( dt );
                break;
            case MOVING_TOWARDS_ATTACHMENT:
                stepMovingTowardsAttachmentStateInTime( dt );
                break;
            case ATTACHED:
                stepAttachedStateInTime( dt );
                break;
            case UNATTACHED_BUT_UNAVAILABLE:
                stepUnattachedButUnavailableStateInTime( dt );
                break;
            default:
                System.out.println( getClass().getName() + " Error: Unrecognized state." );
                assert false;
                attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                break;
        }
    }

    public AttachmentState getAttachmentState() {
        return attachmentState;
    }

    /**
     * Detach the biomolecule from any current attachments.  This will cause
     * the molecule to go into the UNATTACHED_BUT_UNAVAILABLE state for some
     * period of time, then it will become available again.
     */
    protected void detach() {
        assert attachmentSite != null; // Verify internal state is consistent.
        attachmentSite.attachedMolecule.set( null );

    }

    /**
     * Move immediately into the UNATTACHED_AND_AVAILABLE state.  This is
     * generally done only when the user has grabbed the associated molecule.
     */
    public abstract void forceImmediateUnattached();

    /**
     * Step-in-time behavior of the UNATTACHED_AND_AVAILABLE state.
     *
     * @param dt
     */
    protected void stepUnattachedAndAvailableStateInTime( double dt ) {
        assert attachmentSite == null; // Verify internal state is consistent.
        assert attachmentSite.attachedMolecule.get() == new Option.Some<MobileBiomolecule>( biomolecule ); // Verify internal state is consistent.
        attachmentSite = biomolecule.proposeAttachments();
        if ( attachmentSite != null ) {
            // An attachment proposal was accepted, so start heading towards
            // the site.
            biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty, biomolecule.motionBoundsProperty ) );
        }
    }

    /**
     * Step-in-time behavior of the MOVING_TOWARDS_ATTACHMENT state.
     *
     * @param dt
     */
    protected void stepMovingTowardsAttachmentStateInTime( double dt ) {
        assert attachmentSite != null; // Verify internal state is consistent.
        if ( biomolecule.getPosition().equals( attachmentSite.locationProperty.get() ) ) {
            // This molecule is now at the attachment site, so consider it
            // attached.
            attachmentState = AttachmentState.ATTACHED;
            biomolecule.setMotionStrategy( new FollowAttachmentSite( attachmentSite ) );
        }
    }

    /**
     * Step-in-time behavior of the ATTACHED state.  Since the specific
     * attachment behavior is unique for each biomolecule, this method has no
     * default implementation.
     *
     * @param dt
     */
    protected abstract void stepAttachedStateInTime( double dt );

    /**
     * Step-in-time behavior of the UNATTACHED_BUT_UNAVAILABLE state.
     *
     * @param dt
     */
    protected void stepUnattachedButUnavailableStateInTime( double dt ) {
        assert attachmentSite == null; // Verify internal state is consistent.
        detachCountdownTimer -= dt;
        if ( detachCountdownTimer <= 0 ) {
            // Become available for attachment again.
            attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( biomolecule.motionBoundsProperty ) );
        }
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    public enum AttachmentState {
        UNATTACHED_AND_AVAILABLE,
        MOVING_TOWARDS_ATTACHMENT,
        ATTACHED,
        UNATTACHED_BUT_UNAVAILABLE
    }
}
