// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;

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

    // Attachment point.  When this is non-null, the biomolecule is either
    // attached to this point or moving towards attachment with it.
    protected AttachmentSite attachmentSite = null;

    // States used by this state machine.  These are often set by subclasses
    // to non-default values in order to change the default behavior.
    protected AttachmentState unattachedAndAvailableState = new AttachmentState.GenericUnattachedAndAvailableState();
    protected AttachmentState attachedState = new AttachmentState.GenericAttachedState();
    protected AttachmentState movingTowardsAttachmentState = new AttachmentState.GenericMovingTowardsAttachmentState();
    protected AttachmentState unattachedButUnavailableState = new AttachmentState.GenericUnattachedButUnavailableState();

    // Current attachment state.  Changes with each state transition.
    private AttachmentState attachmentState;

    // Direction used when detaching, can be changed by subclasses.  Default is
    // to move up.
    protected Vector2D detachDirection = new Vector2D( 0, 1 );

    // Offset to use when moving towards attachment sites.  This is used when
    // the molecule attaches to an attachment site at some location other than
    // its geometric center.
    protected Vector2D destinationOffset = new Vector2D( 0, 0 );

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
        attachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
        attachmentSite = null;
        biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( detachDirection, biomolecule.motionBoundsProperty ) );
        setState( unattachedButUnavailableState );
    }

    /**
     * Move immediately into the unattached-and-available state.  This is
     * generally done only when the user has grabbed the associated molecule.
     * Calling this when already in this state is harmless.
     */
    public void forceImmediateUnattached() {
        if ( attachmentSite != null ) {
            attachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
        }
        attachmentSite = null;
        setState( unattachedAndAvailableState );
    }

    /**
     * Set a new attachment state.  This calls the "entered" method, so this
     * should be used instead of directly setting the state.
     *
     * @param attachmentState
     */
    public void setState( AttachmentState attachmentState ) {
        this.attachmentState = attachmentState;
        this.attachmentState.entered( this );
    }

    protected void setDestinationOffset( double x, double y ) {
        destinationOffset.setComponents( x, y );
    }

    protected void setDestinationOffset( ImmutableVector2D offset ) {
        setDestinationOffset( offset.getX(), offset.getY() );
    }
}
