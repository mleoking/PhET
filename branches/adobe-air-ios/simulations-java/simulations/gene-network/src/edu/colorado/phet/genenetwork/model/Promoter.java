// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;


import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John
 */
public abstract class Promoter extends SimpleModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static float HEIGHT = 2.5f;  // In nanometers.
    public static float WIDTH = 10;      // In nanometers.
    private static final Dimension2D RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET = new PDimension( 0, HEIGHT / 2 );
    private static final double ATTACH_TO_RNA_POLYMERASE_TIME = 0.5;   // In seconds.
    private static final double DEFAULT_ATTACHMENT_RECOVERY_TIME = 3; // In seconds.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    protected RnaPolymerase rnaPolymeraseAttachmentPartner = null;
    protected AttachmentState rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    protected double rnaPolymeraseAttachmentCountdownTimer;
    protected double recoveryCountdownTimer;
    protected double attachmentRecoveryTime = DEFAULT_ATTACHMENT_RECOVERY_TIME;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public Promoter( IGeneNetworkModelControl model, Point2D initialPosition, Paint paint,
                     boolean fadeIn, double existenceTime ) {

        super( model, createShape(), new Point2D.Double(), paint, false, Double.POSITIVE_INFINITY );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    @Override
    public void stepInTime( double dt ) {

        if ( !isUserControlled() ) {
            switch( rnaPolymeraseAttachmentState ) {
                case UNATTACHED_AND_AVAILABLE:
                    attemptToStartAttaching();
                    break;
                case MOVING_TOWARDS_ATTACHMENT:
                    checkAttachmentCompleted();
                    break;
                case ATTACHED:
                    checkReadyToDetach( dt );
                    break;
                case UNATTACHED_BUT_UNAVAILABLE:
                    checkIfReadyToAttach( dt );
                    break;
                default:
                    // Should never get here, should be debugged if it does.
                    assert false;
                    break;
            }
            super.stepInTime( dt );
        }
    }

    /**
     * This method is used when a molecule of RNA polymerase was recently attached,
     * then detached, and wants to attach again after a short period.  This was
     * created for the case where the RNA polymerase is blocked from traversing the
     * DNA strand and so does some sort of "bumping" against the blocking element.
     * <p/>
     * This should NOT be used by a polymerase molecule that wants to attach but
     * was not recently attached - use the requestImmediateAttach method for that.
     *
     * @param rnaPolymerase
     * @return
     */
    public boolean requestReattach( RnaPolymerase rnaPolymerase ) {

        boolean reattachRequestAccepted = false;

        if ( rnaPolymeraseAttachmentPartner == null &&
             rnaPolymeraseAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVAILABLE ) {
            // We are in the correct state to accept the reattachment.  Note
            // that we trust the molecule to have been recently attached and
            // we don't verify it.
            if ( rnaPolymerase.considerProposalFrom( this ) ) {
                rnaPolymeraseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
                rnaPolymeraseAttachmentPartner = rnaPolymerase;
                reattachRequestAccepted = true;
            }
            else {
                // This should never happen, because the RNA polymerase
                // requested reattachment.
                assert false;
            }
        }

        return reattachRequestAccepted;
    }

    /**
     * This method is used when a molecule of RNA polymerase wants to attach right
     * away to this promoter.  The intended use case is when the user is trying to
     * manually place the RNA polymerase on the promoter.
     * <p/>
     * This should NOT be used by a polymerase molecule that was recently attached
     * and now wants to reattach because its traversal of the DNA is blocked - use
     * requestReattach for that.
     *
     * @param rnaPolymerase
     * @return
     */
    public boolean requestImmediateAttach( RnaPolymerase rnaPolymerase ) {

        boolean attachRequestAccepted = false;

        if ( rnaPolymeraseAttachmentState != AttachmentState.ATTACHED ) {
            // We are not attached to any polymerase, so we are in the correct
            // state to accept this request.
            if ( rnaPolymeraseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                if ( rnaPolymeraseAttachmentPartner != rnaPolymerase ) {
                    // We had something going with a different polymerase,
                    // so we need to terminate that relationship before going
                    // any further.  Nothing worse than polymerase polygamy.
                    rnaPolymeraseAttachmentPartner.detach( this );
                    rnaPolymeraseAttachmentPartner = null;
                    rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                }
            }
            // Initiate the attachment process to the RNA polymerase.
            if ( !rnaPolymerase.considerProposalFrom( this ) ) {
                // This should never happen, because the RNA polymerase
                // requested attachment.
                System.err.println( getClass().getName() + "- Error: Proposal refused by element that requested attachment." );
                assert false;
            }
            // Everything should now be clear for finalizing the actual attachment.
            rnaPolymeraseAttachmentPartner = rnaPolymerase;
            completeAttachmentOfRnaPolymerase();
            attachRequestAccepted = true;
        }

        return attachRequestAccepted;
    }

    public void setDragging( boolean dragging ) {
        if ( dragging == true ) {
            if ( rnaPolymeraseAttachmentPartner != null ) {
                // Reaching this point in the code indicates that the user is
                // dragging this node, but an RNA polymerase is either
                // attached or about to be attached.  This RNA polymerase
                // should be released.
                rnaPolymeraseAttachmentPartner.detach( this );
                rnaPolymeraseAttachmentPartner = null;

                // Set our state to unattached and available.  This is safe
                // because we won't be stepped again until we are released.
                rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        super.setDragging( dragging );
    }

    /**
     * Get the location in absolute space of the attachment point for the specified
     * type of model element.
     */
    public Point2D getAttachmentPointLocation( RnaPolymerase rnaPolymerase ) {
        return new Point2D.Double( getPositionRef().getX() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getWidth(),
                                   getPositionRef().getY() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getHeight() );
    }

    /**
     * Release any relationship that we have with a specific RNA polymerase
     * molecule.  This was created to allow RNA polymerase to terminate the
     * relationship when it is grabbed by the user.
     *
     * @param rnaPolymerase
     */
    public void detach( RnaPolymerase rnaPolymerase ) {
        assert rnaPolymerase == rnaPolymeraseAttachmentPartner;
        rnaPolymeraseAttachmentPartner = null;
        rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        recoveryCountdownTimer = attachmentRecoveryTime;
    }

    @Override
    public boolean isPartOfDnaStrand() {
        return true;
    }

    /**
     * Set the amount of time that should elapse after an RNA polymerase is
     * released before looking for the next possible attachment partner.
     *
     * @param attachmentRecoveryTime
     */
    protected void setAttachmentRecoveryTime( double attachmentRecoveryTime ) {
        this.attachmentRecoveryTime = attachmentRecoveryTime;
    }

    protected void attemptToStartAttaching() {
        if ( !isOkayToAttachToRnaPoly() ) {
            // Something must have changed making it no longer okay to
            // try to attach to the RNA poly, so change to the state where we
            // are not available.
            rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
        else {
            assert rnaPolymeraseAttachmentPartner == null;
            // Search for a partner to attach to.
            RnaPolymerase rnaPolymerase = getModel().getNearestFreeRnaPolymerase( getPositionRef() );
            if ( rnaPolymerase != null ) {
                if ( rnaPolymerase.considerProposalFrom( this ) ) {
                    // Attachment formed.
                    rnaPolymeraseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
                    rnaPolymeraseAttachmentPartner = rnaPolymerase;
                }
            }
        }
    }

    protected boolean isOkayToAttachToRnaPoly() {
        return true;
    }

    protected void checkAttachmentCompleted() {
        assert rnaPolymeraseAttachmentPartner != null;

        if ( isOkayToAttachToRnaPoly() ) {
            // Calculate the current location of our RnaPolymerase attachment point.
            Point2D rnaPolymeraseAttachmentPtLocation =
                    new Point2D.Double( getPositionRef().getX() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getWidth(),
                                        getPositionRef().getY() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getHeight() );

            // Check the distance between the attachment points.
            if ( rnaPolymeraseAttachmentPtLocation.distance(
                    rnaPolymeraseAttachmentPartner.getLacPromoterAttachmentPointLocation() ) < ATTACHMENT_FORMING_DISTANCE ) {
                // Close enough to attach.
                completeAttachmentOfRnaPolymerase();
            }
        }
        else {
            // Conditions have changed and it is no longer okay to continue to
            // try to attach.  Release the RNA Polymerase.
            rnaPolymeraseAttachmentPartner.detach( this );
            rnaPolymeraseAttachmentPartner = null;
            rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
    }

    private void completeAttachmentOfRnaPolymerase() {
        rnaPolymeraseAttachmentPartner.attach( this );
        rnaPolymeraseAttachmentState = AttachmentState.ATTACHED;
        rnaPolymeraseAttachmentCountdownTimer = ATTACH_TO_RNA_POLYMERASE_TIME;
    }

    protected void checkReadyToDetach( double dt ) {
        assert rnaPolymeraseAttachmentPartner != null;

        rnaPolymeraseAttachmentCountdownTimer -= dt;

        if ( rnaPolymeraseAttachmentCountdownTimer <= 0 ) {
            // It's time to detach.
            rnaPolymeraseAttachmentPartner.detach( this );
            rnaPolymeraseAttachmentPartner = null;
            recoveryCountdownTimer = attachmentRecoveryTime;
            rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
    }

    private void checkIfReadyToAttach( double dt ) {
        recoveryCountdownTimer -= dt;
        if ( recoveryCountdownTimer < 0 ) {
            if ( isOkayToAttachToRnaPoly() ) {
                rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
            else {
                // Keep the counter from decrementing far enough to roll over.
                recoveryCountdownTimer = 0;
            }
        }
    }

    private static Shape createShape() {

        GeneralPath outline = new GeneralPath();

        outline.moveTo( WIDTH / 2, HEIGHT / 2 );
        outline.lineTo( WIDTH / 2, -HEIGHT / 2 );
        outline.lineTo( -WIDTH / 2, -HEIGHT / 2 );
        outline.lineTo( -WIDTH / 2, HEIGHT / 2 );
        outline.lineTo( -WIDTH / 4, 0 );
        outline.lineTo( 0, HEIGHT / 2 );
        outline.lineTo( WIDTH / 4, 0 );
        outline.closePath();

        return outline;
    }
}
