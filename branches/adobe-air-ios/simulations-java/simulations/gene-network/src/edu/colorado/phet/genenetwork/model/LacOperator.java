// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacI, which in real life is a protein that inhibits
 * (hence the 'I' in the name) the expression of genes coding for proteins
 * involved in lactose metabolism in bacteria.
 *
 * @author John Blanco
 */
public class LacOperator extends SimpleModelElement {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Paint ELEMENT_PAINT = new Color( 200, 200, 200 );
    private static final double WIDTH = 7;   // In nanometers.
    private static final double HEIGHT = 3;  // In nanometers.
    private static final Dimension2D LAC_I_ATTACHMENT_POINT_OFFSET = new PDimension( 0, HEIGHT / 2 );
    private static final Dimension2D ATTACHMENT_REGION_SIZE = new PDimension( WIDTH * 0.5, HEIGHT / 2 );
    private static double LAC_I_ATTACHMENT_TIME = 15; // In seconds.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private LacI lacIAttachmentPartner = null;
    private AttachmentState lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    private double lacIAttachmentCountdownTimer;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public LacOperator( IGeneNetworkModelControl model, Point2D initialPosition ) {
        super( model, createShape(), initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY );
        // Add binding point for LacI.
    }

    public LacOperator( IGeneNetworkModelControl model ) {
        this( model, new Point2D.Double() );
    }

    public LacOperator() {
        this( null, new Point2D.Double() );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    @Override
    public void stepInTime( double dt ) {

        if ( !isUserControlled() ) {
            switch( lacIAttachmentState ) {
                case UNATTACHED_AND_AVAILABLE:
                    attemptToStartAttaching();
                    break;
                case MOVING_TOWARDS_ATTACHMENT:
                    checkAttachmentCompleted();
                    break;
                case ATTACHED:
                    checkTimeToDetach( dt );
                    break;
                case UNATTACHED_BUT_UNAVAILABLE:
                    checkBecomeAvailable( dt );
                    break;
                default:
                    // Should never get here, should be debugged if it does.
                    assert false;
                    break;
            }
            super.stepInTime( dt );
        }
    }

    @Override
    public void setDragging( boolean dragging ) {
        if ( dragging == true ) {
            if ( lacIAttachmentPartner != null ) {
                // Reaching this point in the code indicates that the user is
                // dragging this node, but a lac I is either attached or one
                // its way to us.  This lac I should be released.
                lacIAttachmentPartner.detach( this );
                lacIAttachmentPartner = null;

                // Set our state to unattached and available.  This is safe
                // because we won't be stepped again until we are released.
                lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        super.setDragging( dragging );
    }

    public boolean isLacIAttached() {
        return ( lacIAttachmentState == AttachmentState.ATTACHED );
    }

    private void attemptToStartAttaching() {
        assert lacIAttachmentPartner == null;

        // Don't initiate an attachment if RNA polymerse is on the lac
        // operator.  This is a bit of "hollywooding" to avoid a situation
        // where the LacI swoops in just as the RNA poly starts to traverse,
        // making it look like the RNA poly traverses right over the LacI,
        // which is confusing to users.
        LacPromoter lacPromoter = getModel().getLacPromoter();
        if ( lacPromoter != null && lacPromoter.isInContactWithRnaPolymerase() ) {
            // Can't attach now.
            lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
        else {
            // Okay to attach to LacI - search for a partner to attach to.
            LacI lacI = getModel().getNearestFreeLacI( getPositionRef() );
            if ( lacI != null ) {
                if ( lacI.considerProposalFrom( this ) ) {
                    // Attachment formed.
                    lacIAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
                    lacIAttachmentPartner = lacI;
                }
            }
        }
    }

    private void checkTimeToDetach( double dt ) {
        lacIAttachmentCountdownTimer -= dt;
        if ( lacIAttachmentCountdownTimer <= 0 ) {
            // The countdown has expired, but make sure that there isn't an
            // an RNA polymerase right next to use, since this indicates that
            // it may be trying to traverse, and we don't want to open a short
            // Time to release the lacI, but only if there isn't an RNA
            // polymerase about to traverse.
            boolean noRnaPolyCloseBy = true;
            for ( RnaPolymerase rnaPoly : getModel().getRnaPolymeraseList() ) {
                if ( rnaPoly.getPositionRef().distance( getPositionRef() ) < 15 ) {
                    noRnaPolyCloseBy = false;
                }
            }

            if ( noRnaPolyCloseBy ) {
                // Go ahead and detach to lacI.
                lacIAttachmentPartner.detach( this );
                lacIAttachmentPartner = null;
                lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
            else {
                // Prevent the counter from going too negative.
                lacIAttachmentCountdownTimer = 0;
            }
        }
    }

    private void checkBecomeAvailable( double dt ) {
        if ( !getModel().getLacPromoter().isInContactWithRnaPolymerase() ) {
            // The lac promoter is not in contact with RNA polymerase, so we
            // can seek attachment to LacI.
            lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
        }
    }

    private void checkAttachmentCompleted() {
        assert lacIAttachmentPartner != null;

        LacPromoter lacPromoter = getModel().getLacPromoter();
        if ( lacPromoter != null && lacPromoter.isInContactWithRnaPolymerase() ) {
            // An instance of LacI is moving towards this lac operator, but
            // RNA polymerase is now on the promoter.  This can create a
            // visually confusing situation where the LacI attaches just as
            // the RNA polymerase is starting to traverse the DNA, making it
            // look like the polymerase goes right over the LacI.  To avoid
            // this, we stop the LacI from moving towards us.
            lacIAttachmentPartner.detach( this );
            lacIAttachmentPartner = null;
            lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
        else {
            // Calculate the current location of our LacI attachment point.
            Point2D lacIAttachmentPtLocation =
                    new Point2D.Double( getPositionRef().getX() + LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
                                        getPositionRef().getY() + LAC_I_ATTACHMENT_POINT_OFFSET.getHeight() );

            // Check the distance between the attachment points.
            if ( lacIAttachmentPtLocation.distance( lacIAttachmentPartner.getAttachmentPointLocation( this ) ) < ATTACHMENT_FORMING_DISTANCE ) {
                // Close enough to attach.
                lacIAttachmentPartner.attach( this );
                lacIAttachmentState = AttachmentState.ATTACHED;
            }

            // Set the countdown timer that will control how long these remain
            // attached to one another.
            lacIAttachmentCountdownTimer = LAC_I_ATTACHMENT_TIME;
        }
    }

    private static Shape createShape() {

        Rectangle2D baseRect = new Rectangle2D.Double( -WIDTH / 2, -HEIGHT / 2, WIDTH,
                                                       HEIGHT - ATTACHMENT_REGION_SIZE.getHeight() );
        Rectangle2D attachmentRegion = new Rectangle2D.Double( -ATTACHMENT_REGION_SIZE.getWidth() / 2,
                                                               baseRect.getMaxY(), ATTACHMENT_REGION_SIZE.getWidth(), ATTACHMENT_REGION_SIZE.getHeight() );
        Area area = new Area( baseRect );
        area.add( new Area( attachmentRegion ) );

        return area;
    }

    public static Dimension2D getBindingRegionSize() {
        return ATTACHMENT_REGION_SIZE;
    }

    /**
     * Get the location in absolute space of the attachment point for the specified
     * type of model element.
     */
    public Point2D getLacIAttachmentPointLocation() {
        return new Point2D.Double( getPositionRef().getX() + LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
                                   getPositionRef().getY() + LAC_I_ATTACHMENT_POINT_OFFSET.getHeight() );
    }

    public AttachmentState getLacIAttachmentState() {
        return lacIAttachmentState;
    }

    public void detach( LacI lacI ) {
        if ( lacI != lacIAttachmentPartner ) {
            System.err.println( getClass().getName() + " - Warning: Request to disconnect received from non-partner." );
            return;
        }

        lacIAttachmentPartner = null;
        lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    }

    /**
     * This method is used when a LacI protein wants to attach right away to the
     * lac operator.  The intended use case is when the user tries to manually
     * place a LacI on the promoter.
     *
     * @param lacI
     * @return
     */
    public boolean requestImmediateAttach( LacI lacI ) {

        boolean attachRequestAccepted = false;

        if ( lacIAttachmentState != AttachmentState.ATTACHED ) {
            // We are not attached to any LacI, so we are in the correct state
            // to accept this request.
            if ( lacIAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                if ( lacIAttachmentPartner != lacI ) {
                    // We had something going with a different lacI, so we
                    // need to terminate that relationship before going any
                    // further.
                    lacIAttachmentPartner.detach( this );
                    lacIAttachmentPartner = null;
                    lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                }
            }
            // Initiate the attachment process to the LacI.
            if ( !lacI.considerProposalFrom( this ) ) {
                // This should never happen, because the lacI requested the
                // attachment.
                System.err.println( getClass().getName() + "- Error: Proposal refused by element that requested attachment." );
                assert false;
            }
            // Everything should now be clear for finalizing the actual attachment.
            lacIAttachmentPartner = lacI;
            lacIAttachmentPartner.attach( this );
            lacIAttachmentState = AttachmentState.ATTACHED;
            lacIAttachmentCountdownTimer = LAC_I_ATTACHMENT_TIME;

            attachRequestAccepted = true;
        }

        return attachRequestAccepted;
    }

    @Override
    public boolean isPartOfDnaStrand() {
        return true;
    }

    public static Dimension2D getLacIAttachementPointOffset() {
        return new PDimension( LAC_I_ATTACHMENT_POINT_OFFSET );
    }

    @Override
    protected boolean isInAllowableLocation() {
        // Find out if we are within range of our location on the DNA strand.
        return getPositionRef().distance( getModel().getDnaStrand().getLacOperatorLocation() ) < LOCK_TO_DNA_DISTANCE;
    }

    @Override
    protected Point2D getDefaultLocation() {
        return getModel().getDnaStrand().getLacOperatorLocation();
    }
}
