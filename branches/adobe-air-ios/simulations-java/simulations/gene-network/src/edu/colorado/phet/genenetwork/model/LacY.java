// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacY, which is the model element that transports
 * lactose from outside the cell membrane to inside.
 *
 * @author John Blanco
 */
public class LacY extends SimpleModelElement {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double SIZE = 8; // In nanometers.
    private static final Paint ELEMENT_PAINT = new GradientPaint( new Point2D.Double( -SIZE, 0 ),
                                                                  new Color( 0, 200, 0 ), new Point2D.Double( SIZE * 5, 0 ), Color.WHITE );
    private static final double EXISTENCE_TIME = 45; // Seconds.

    // Attachment point for glucose.  Note that glucose generally only
    // attaches when it is bound up in lactose, so this is essentially the
    // lactose offset too.
    private static final Dimension2D GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension( 0, SIZE / 2 );

    // Amount of time that it takes for lactose to traverse from the
    // attachment point to the other side.
    private static final double LACTOSE_TRAVERSAL_TIME = 0.5;  // In seconds.

    // Amount of time after releasing one lactose molecule until it is okay
    // to start trying to attach to another.  This is needed to prevent the
    // LacY from getting into a state where it can never fade out.
    private static final double RECOVERY_TIME = 0.150;  // In seconds.

    // These are used to determine whether a lactose molecule is close enough
    // that this molecule should try to grab it after it has been moved by
    // the user.
    private static final double LACTOSE_ATTACHMENT_FORMING_DISTANCE = 2; // In nanometers.

    // Distance within which we will snap to the membrane if dropped there by
    // the user.
    private static final double SNAP_TO_MEMBRANE_DISTANCE = 5;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private Glucose glucoseAttachmentPartner = null;
    private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    private double lactoseTraversalCoundownTimer;
    private double recoverCountdownTimer;
    private double lactoseTraversalDistance;
    private Point2D membraneDestination = new Point2D.Double();

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public LacY( IGeneNetworkModelControl model, Point2D initialPosition, boolean fadeIn ) {
        super( model, createShape(), initialPosition, ELEMENT_PAINT, fadeIn, EXISTENCE_TIME );
        if ( model != null ) {
            setMotionStrategy( new StillnessMotionStrategy() );
            // Set bounds that will prevent the user from dragging this below the
            // DNA.
            Rectangle2D dragBounds = getModel().getInteriorMotionBounds();
            dragBounds.add( new Point2D.Double( dragBounds.getX(), dragBounds.getMaxY() + this.getShape().getBounds2D().getHeight() ) );
            setDragBounds( dragBounds );
        }
        lactoseTraversalDistance = getShape().getBounds2D().getHeight();
    }

    public LacY( IGeneNetworkModelControl model, boolean fadeIn ) {
        this( model, new Point2D.Double(), fadeIn );
    }

    public LacY() {
        this( null, false );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    @Override
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if ( !isUserControlled() ) {
            if ( getExistenceState() == ExistenceState.EXISTING &&
                 glucoseAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
                 isEmbeddedInMembrane() ) {

                // Look for some lactose to attach to.
                glucoseAttachmentPartner =
                        getModel().getNearestLactose( getPositionRef(), PositionWrtCell.OUTSIDE_CELL, true );

                getEngagedToLactose();
            }
            else if ( glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                // See if the glucose is close enough to finalize the attachment.
                if ( getGlucoseAttachmentPointLocation().distance( glucoseAttachmentPartner.getLacYAttachmentPointLocation() ) < LACTOSE_ATTACHMENT_FORMING_DISTANCE ) {
                    // Finalize the attachment.
                    completeAttachmentOfGlucose();
                }
            }
            else if ( glucoseAttachmentState == AttachmentState.ATTACHED ) {
                // Move the lactose to the appropriate location based on the
                // amount of time that it has been attached.  This is how the
                // lactose moves through the cell membrane.
                lactoseTraversalCoundownTimer -= dt;
                double distanceTraveled = ( 1 - lactoseTraversalCoundownTimer / LACTOSE_TRAVERSAL_TIME ) * lactoseTraversalDistance;
                glucoseAttachmentPartner.setPosition( glucoseAttachmentPartner.getPositionRef().getX(),
                                                      getGlucoseAttachmentPointLocation().getY() - distanceTraveled );
                if ( lactoseTraversalCoundownTimer <= 0 ) {
                    // The lactose has fully traversed the membrane.  Release
                    // it - it should now be inside the cell.
                    glucoseAttachmentPartner.detach( this );
                    glucoseAttachmentPartner.setMotionStrategy( new InjectionMotionStrategy(
                            MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), glucoseAttachmentPartner ),
                            new Vector2D( 0, -15 ) ) );
                    glucoseAttachmentPartner.setUpDraggableBounds( PositionWrtCell.INSIDE_CELL );
                    glucoseAttachmentPartner = null;
                    glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
                    recoverCountdownTimer = RECOVERY_TIME;
                    setOkayToFade( true );
                }
            }
            else if ( glucoseAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVAILABLE ) {
                recoverCountdownTimer -= dt;
                if ( recoverCountdownTimer <= 0 ) {
                    // Recovery is complete.
                    glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                }
            }
        }
    }

    private void getEngagedToLactose() {

        if ( glucoseAttachmentPartner != null ) {
            // We found a lactose that is free, so start the process of
            // attaching to it.
            if ( glucoseAttachmentPartner.considerProposalFrom( this ) != true ) {
                assert false;  // As designed, this should always succeed, so debug if it doesn't.
            }
            else {
                glucoseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;

                // Prevent fadeout from occurring while attached to lactose.
                setOkayToFade( false );
            }
        }
    }

    @Override
    public void setDragging( boolean dragging ) {
        if ( dragging && !isUserControlled() ) {
            // The user has grabbed this element.  Have it release any pending
            // attachments.
            if ( glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                glucoseAttachmentPartner.detach( this );
                glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
                glucoseAttachmentPartner = null;
                recoverCountdownTimer = 0;  // We are good to reattach as soon as we are released.
            }
        }
        else if ( !dragging && isUserControlled() ) {
            // The user has released this element.
            if ( !isEmbeddedInMembrane() ) {
                // The user had grabbed this before it was embedded in the
                // membrane.  If they have dropped this close to the membrane,
                // assume that they were trying to position it on the membrane
                // and just go straight to it.  If they dropped this some
                // distance from the membrane, resume the motion needed to get
                // to it.
                if ( Math.abs( getPositionRef().getY() - getModel().getCellMembraneRect().getCenterY() ) < SNAP_TO_MEMBRANE_DISTANCE ) {
                    setPosition( getPositionRef().getX(), getModel().getCellMembraneRect().getCenterY() );
                    setMotionStrategy( new StillnessMotionStrategy() );
                }
                else {
                    Rectangle2D motionBounds = getModel().getInteriorMotionBoundsAboveDna();
                    motionBounds.setFrame( motionBounds.getX(), motionBounds.getY(), motionBounds.getWidth(),
                                           motionBounds.getHeight() + getModel().getCellMembraneRect().getHeight() );
                    setMotionStrategy( new LinearMotionStrategy( motionBounds, getPositionRef(), getMembraneDestinationRef(),
                                                                 10 ) );
                }
            }
        }
        super.setDragging( dragging );
    }

    @Override
    public void setPosition( double xPos, double yPos ) {
        // Prevent the user from dragging this out of the membrane once it has
        // been embedded there.
        if ( !isUserControlled() || !isEmbeddedInMembrane() ) {
            super.setPosition( xPos, yPos );
        }
    }

    /**
     * Complete that process of attaching to glucose.  Created to avoid duplication
     * of code.
     */
    private void completeAttachmentOfGlucose() {
        glucoseAttachmentPartner.attach( this );
        glucoseAttachmentState = AttachmentState.ATTACHED;
        // Start the attachment timer/counter.
        lactoseTraversalCoundownTimer = LACTOSE_TRAVERSAL_TIME;
    }

    private static Shape createShape() {
        // Start with a circle.
        RoundRectangle2D startingShape = new RoundRectangle2D.Double( -SIZE / 2, -SIZE / 2, SIZE, SIZE, SIZE / 3, SIZE / 3 );
        Area area = new Area( startingShape );

        // Get the shape of a lactose molecule and shift it to the appropriate
        // position.
        Shape lactoseShape = Lactose.getShape();
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation( 0, SIZE / 2 );
        lactoseShape = transform.createTransformedShape( lactoseShape );

        // Subtract off the shape of the lactose molecule.
        area.subtract( new Area( lactoseShape ) );
        return area;
    }

    @Override
    protected void onTransitionToExistingState() {
        // Pick a point on the cell membrane and set a motion strategy to get
        // there.
        // Extend the motion bounds to allow this to move into the membrane.
        Rectangle2D motionBounds = getModel().getInteriorMotionBoundsAboveDna();
        motionBounds.setFrame( motionBounds.getX(), motionBounds.getY(), motionBounds.getWidth(),
                               motionBounds.getHeight() + getModel().getCellMembraneRect().getHeight() );
        // Set a motion strategy that will move this LacY to a spot on the
        // membrane.
        setMembraneDestination( getModel().getOpenSpotForLacY() );
        setMotionStrategy( new LinearMotionStrategy( motionBounds, getPositionRef(), getMembraneDestinationRef(), 14 ) );
    }

    /**
     * Force a detach from the glucose molecule.  This was created to support the
     * case where the user grabs the glucose molecule when it is attached to LacY,
     * but may have other uses.
     *
     * @param glucose
     */
    public void detach( Glucose glucose ) {

        // Make sure we are in the expect state.
        assert glucose == glucoseAttachmentPartner;
        assert glucoseAttachmentState == AttachmentState.ATTACHED || glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;

        // Clear the state variables that track attachment to lactose.
        glucoseAttachmentPartner = null;
        glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        recoverCountdownTimer = RECOVERY_TIME;

        // It is now okay for the LacY to fade out of existence if it needs to.
        setOkayToFade( true );
    }

    /**
     * Request an immediate attachment for a glucose molecule (which should be half
     * of a lactose molecule).  This is generally used when the glucose was
     * manually moved by the user to a location that is quite close to this lacY.
     *
     * @param glucose
     * @return true if it can attach, false if it already has a different partner.
     */
    public boolean requestImmediateAttach( Glucose glucose ) {

        // Shouldn't get a request for its current partner.
        assert glucose != glucoseAttachmentPartner;

        if ( glucoseAttachmentState == AttachmentState.ATTACHED ) {
            // We are already attached to a glucose molecule, so we can attach
            // to a different one.
            return false;
        }

        if ( glucoseAttachmentPartner != null ) {
            // We were moving towards attachment to a different glucose, so
            // break off the engagement.
            glucoseAttachmentPartner.detach( this );
        }

        // Attach to this new glucose molecule.
        if ( glucose.considerProposalFrom( this ) != true ) {
            // This should never happen, since the glucose requested the
            // attachment, so it should be debuged if it does.
            System.err.println( getClass().getName() + "- Error: Proposal refused by element that requested attachment." );
            assert false;
        }

        // Everything should now be clear for finalizing the actual attachment.
        glucoseAttachmentPartner = glucose;
        completeAttachmentOfGlucose();

        return true;
    }

    /**
     * Get the location in absolute space of the attachment point for this type of
     * model element.
     */
    public Point2D getGlucoseAttachmentPointLocation() {
        return new Point2D.Double( getPositionRef().getX() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getWidth(),
                                   getPositionRef().getY() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getHeight() );
    }

    public static Dimension2D getGlucoseAttachmentPointOffset() {
        return new PDimension( GLUCOSE_ATTACHMENT_POINT_OFFSET );
    }

    private static final double ERROR_TOLERENCE = 0.01;

    public boolean isEmbeddedInMembrane() {
        boolean isEmbeddedInMembrane = false;
        Rectangle2D cellMembraneRect = getModel().getCellMembraneRect();
        if ( cellMembraneRect != null &&
             getPositionRef().getY() + ERROR_TOLERENCE > cellMembraneRect.getCenterY() &&
             getPositionRef().getY() - ERROR_TOLERENCE < cellMembraneRect.getCenterY() ) {

            isEmbeddedInMembrane = true;
        }

        return isEmbeddedInMembrane;
    }

    protected Point2D getMembraneDestinationRef() {
        return membraneDestination;
    }

    protected void setMembraneDestination( Point2D membraneDestination ) {
        this.membraneDestination.setLocation( membraneDestination );
    }
}
