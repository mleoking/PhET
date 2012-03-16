// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 *
 * @author John Blanco
 */
public class LacZ extends SimpleModelElement {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double SIZE = 10; // In nanometers.
    private static final Paint ELEMENT_PAINT = new GradientPaint( new Point2D.Double( -SIZE, 0 ),
                                                                  new Color( 185, 147, 187 ), new Point2D.Double( SIZE * 5, 0 ), Color.WHITE );
    private static final double EXISTENCE_TIME = 25; // Seconds.

    // Attachment point for glucose.  Note that glucose generally only
    // attaches when it is bound up in lactose, so this is essentially the
    // lactose offset too.
    private static final Dimension2D GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension( 0, -SIZE / 2 );

    // Amount of time that lactose is attached before it is "digested",
    // meaning that it is broken apart and released.
    private static final double LACTOSE_ATTACHMENT_TIME = 0.5;  // In seconds.

    // Amount of time after releasing one lactose molecule until it is okay
    // to start trying to attach to another.  This is needed to prevent the
    // LacZ from getting into a state where it can never fade out.
    private static final double RECOVERY_TIME = 0.250;  // In seconds.

    // These are used to determine whether a lactose molecule is close enough
    // that this molecule should try to grab it after it has been moved by
    // the user.
    private static final double LACTOSE_IMMEDIATE_GRAB_DISTANCE = 7; // In nanometers.
    private static final double LACTOSE_GRAB_DISTANCE = 15; // In nanometers.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private Glucose glucoseAttachmentPartner = null;
    private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    private double lactoseAttachmentCountdownTimer;
    private double recoverCountdownTimer;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    /**
     * Main constructor for LacZ.
     */
    public LacZ( IGeneNetworkModelControl model, Point2D initialPosition, boolean fadeIn ) {
        super( model, createShape(), initialPosition, ELEMENT_PAINT, fadeIn, generateExistenceTime( model ) );

        if ( model != null ) {
            setMotionStrategy( new StillnessMotionStrategy() );
            // Set bounds that will prevent the user from dragging this below the
            // DNA or above the cell membrane.
            setDragBounds( getModel().getInteriorMotionBounds() );
        }
    }

    public LacZ( IGeneNetworkModelControl model, boolean fadeIn ) {
        this( model, new Point2D.Double(), fadeIn );
    }

    public LacZ() {
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
                 glucoseAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE ) {

                // Look for some lactose to attach to.
                glucoseAttachmentPartner =
                        getModel().getNearestLactose( getPositionRef(), PositionWrtCell.INSIDE_CELL, true );

                getEngagedToLactose();

            }
            else if ( glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                // See if the glucose is close enough to finalize the attachment.
                if ( getGlucoseAttachmentPointLocation().distance( glucoseAttachmentPartner.getLacZAttachmentPointLocation() ) < ATTACHMENT_FORMING_DISTANCE ) {
                    // Finalize the attachment.
                    completeAttachmentOfGlucose();
                }
            }
            else if ( glucoseAttachmentState == AttachmentState.ATTACHED ) {
                lactoseAttachmentCountdownTimer -= dt;
                if ( lactoseAttachmentCountdownTimer <= 0 ) {
                    // Time to break down and release the lactose.
                    glucoseAttachmentPartner.releaseGalactose();
                    glucoseAttachmentPartner.detach( this );
                    glucoseAttachmentPartner = null;
                    glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
                    setOkayToFade( true );
                    recoverCountdownTimer = RECOVERY_TIME;
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

                // Move towards the partner.
                Dimension2D offsetFromTarget = new PDimension(
                        Glucose.getLacZAttachmentPointOffset().getWidth() - getGlucoseAttachmentPointOffset().getWidth(),
                        Glucose.getLacZAttachmentPointOffset().getHeight() - getGlucoseAttachmentPointOffset().getHeight() );
                setMotionStrategy( new CloseOnMovingTargetMotionStrategy( glucoseAttachmentPartner, offsetFromTarget,
                                                                          MotionBoundsTrimmer.trim( getModel().getInteriorMotionBounds(), this ) ) );
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
            // The user has released this element.  See if there are any
            // potential partners nearby.
            if ( glucoseAttachmentPartner == null ) {
                checkForNearbyLactoseToGrab();
            }
        }
        super.setDragging( dragging );
    }

    /**
     * Complete that process of attaching to glucose.  Created to avoid duplication
     * of code.
     */
    private void completeAttachmentOfGlucose() {
        glucoseAttachmentPartner.attach( this );
        glucoseAttachmentState = AttachmentState.ATTACHED;
        setMotionStrategy( new RandomWalkMotionStrategy( MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
        // Start the attachment timer/counter.
        lactoseAttachmentCountdownTimer = LACTOSE_ATTACHMENT_TIME;
    }

    private static Shape createShape() {
        // Start with a circle.
        Ellipse2D startingShape = new Ellipse2D.Double( -SIZE / 2, -SIZE / 2, SIZE, SIZE );
        Area area = new Area( startingShape );

        // Get the shape of a lactose molecule and shift it to the appropriate
        // position.
        Shape lactoseShape = Lactose.getShape();
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation( 0, -SIZE / 2 );
        lactoseShape = transform.createTransformedShape( lactoseShape );

        // Subtract off the shape of the lactose molecule.
        area.subtract( new Area( lactoseShape ) );
        return area;
    }

    @Override
    protected void onTransitionToExistingState() {
        setMotionStrategy( new RandomWalkMotionStrategy(
                MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
    }

    /**
     * Force a detach from the glucose molecule.  This was created to support the
     * case where the user grabs the glucose molecule when it is attached to LacI,
     * but may have other used.
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

        // It is now okay for the LacZ to fade out of existence if it needs to.
        setOkayToFade( true );
    }

    /**
     * Request an immediate attachment for a glucose molecule (which should be half
     * of a lactose molecule).  This is generally used when the glucose was
     * manually moved by the user to a location that is quite close to this lacI.
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

    /**
     * Check if there is any lactose in the immediate vicinity and, if so, attempt
     * to establish a pending attachment with it.  This is generally used when the
     * user moves this element manually to some new location.
     */
    private void checkForNearbyLactoseToGrab() {
        assert glucoseAttachmentPartner == null;  // Shouldn't be doing this if we already have a partner.
        Glucose nearestLactose = getModel().getNearestLactose( getPositionRef(), PositionWrtCell.INSIDE_CELL, false );
        if ( nearestLactose != null && nearestLactose.getPositionRef().distance( getPositionRef() ) < LACTOSE_GRAB_DISTANCE ) {
            if ( nearestLactose.breakOffPendingAttachments( this ) ) {
                // Looks like the lactose can be grabbed.
                glucoseAttachmentPartner = nearestLactose;
                getEngagedToLactose();
                if ( nearestLactose.getPositionRef().distance( getPositionRef() ) < LACTOSE_IMMEDIATE_GRAB_DISTANCE ) {
                    // Attach right now.
                    completeAttachmentOfGlucose();
                }
            }
        }
    }

    /**
     * Generate the existence time for a LacZ.  This was needed as a bit of
     * "Hollywooding" - if the lactose level gets really high because LacZ
     * production is disabled, when it is turned back on, it remains stable but
     * high.  We wanted it to actually come back down to a fairly low level.  This
     * makes LacZ stick around longer, and hence break down more lactose, when
     * there is more present, and should serve to help reduce the level.
     *
     * @param model
     * @return
     */
    private static double generateExistenceTime( IGeneNetworkModelControl model ) {

        // Factors that can be adjusted for different behavior.
        int lactoseThreshold = 20;
        double multiplier = 2;

        // Calculate the existence time.
        double existenceTime;
        if ( model == null || model.getLactoseLevel() < lactoseThreshold ) {
            existenceTime = EXISTENCE_TIME;
        }
        else {
            existenceTime = EXISTENCE_TIME * multiplier;
        }

        return existenceTime;
    }
}
