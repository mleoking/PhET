// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
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
public class LacI extends SimpleModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants that control size and appearance.
    private static final Paint ELEMENT_PAINT = new Color( 200, 200, 200 );
    private static double WIDTH = 7;   // In nanometers.
    private static double HEIGHT = 4;  // In nanometers.

    // Attachment point offset for attaching to lac operator.
    private static PDimension LAC_OPERATOR_ATTACHMENT_POINT_OFFSET =
            new PDimension( 0, -HEIGHT / 2 + LacOperator.getBindingRegionSize().getHeight() );

    // Attachment point offset for attaching to glucose.
    private static PDimension GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension( 0, HEIGHT / 2 );

    // Time definition for the amount of time that this is unavailable after
    // detaching.  Prevents instant detach/re-attach cycles.
    private static double RECOVERY_TIME_FOR_LAC_OPERATOR_ATTACHMENT = 5; // In seconds.
    private static double RECOVERY_TIME_FOR_LACTOSE_ATTACHMENT = 2; // In seconds.

    // Time of existence.
    private static final double EXISTENCE_TIME = 30; // Seconds.

    // Amount of time that this element attaches to lactose.
    private static final double LACTOSE_ATTACHMENT_TIME = 15;

    // Point where the lacI heads towards when it is first created.  This was
    // needed due to a tendency for it to hang around the general area where
    // it was spawned and thus be a long way from the DNA binding region.
    // This point is empirically determined, tweak as needed.
    private static final Point2D INITIAL_DESTINATION_POINT = new Point2D.Double( 20, 0 );

    // Distance within which this should try to attach immediately to the lac
    // operator.
    private static final double LAC_OPERATOR_IMMEDIATE_ATTACH_DISTANCE = 8;

    // These are used to determine whether a lactose molecule is close enough
    // that this molecule should try to grab it after it has been moved by
    // the user.
    private static final double LACTOSE_IMMEDIATE_GRAB_DISTANCE = 7; // In nanometers.
    private static final double LACTOSE_GRAB_DISTANCE = 15; // In nanometers.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private LacOperator lacOperatorAttachmentPartner = null;
    private AttachmentState lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    private Glucose glucoseAttachmentPartner = null;
    private double glucoseAttachmentTimeCountdown = 0;
    private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
    private Point2D targetPositionForLacOperatorAttachment = new Point2D.Double();
    private double recoveryFromLacOperatorAttachmentCountdown = 0;
    private double recoveryFromLactoseAttachmentCountdown = 0;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public LacI( IGeneNetworkModelControl model, Point2D initialPosition, boolean fadeIn ) {
        super( model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT, fadeIn, EXISTENCE_TIME );
        if ( model != null ) {
            setMotionStrategy( new RandomWalkMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            // Set bounds that will prevent the user from dragging this below the
            // DNA or above the cell membrane.
            setDragBounds( getModel().getInteriorMotionBounds() );
        }
    }

    public LacI( IGeneNetworkModelControl model, boolean fadeIn ) {
        this( model, new Point2D.Double(), fadeIn );
    }

    public LacI() {
        this( null, false );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    @Override
    public void setDragging( boolean dragging ) {
        if ( dragging == true && !isUserControlled() ) {
            // The user has grabbed this node and is moving it.  Is it
            // attached to the lac operator?
            if ( lacOperatorAttachmentPartner != null ) {
                // Yes it is, so it needs to detach.
                assert lacOperatorAttachmentState == AttachmentState.ATTACHED || lacOperatorAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
                lacOperatorAttachmentPartner.detach( this );
                lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                lacOperatorAttachmentPartner = null;
                setMotionStrategy( new RandomWalkMotionStrategy( MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            }
            // Does it have any pending relationships with lactose that need
            // to be terminated?
            if ( glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
                glucoseAttachmentPartner.detach( this );
                glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
                glucoseAttachmentPartner = null;
                glucoseAttachmentTimeCountdown = 0;  // We are good to reattach as soon as we are released.
            }

            if ( glucoseAttachmentPartner == null && lacOperatorAttachmentPartner == null ) {
                setOkayToFade( true );
            }
        }
        else if ( dragging == false && isUserControlled() ) {
            // This element has just been released by the user.  It should be
            // considered available.
            lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;

            // If this was dropped within range of the lac operator, it should
            // try to attach to it.
            LacOperator lacOperator = getModel().getLacOperator();
            if ( lacOperator != null &&
                 glucoseAttachmentState != AttachmentState.ATTACHED &&
                 getPositionRef().distance( lacOperator.getPositionRef() ) < LAC_OPERATOR_IMMEDIATE_ATTACH_DISTANCE ) {

                // We are in range, so try to attach.
                lacOperator.requestImmediateAttach( this );
            }
            else if ( glucoseAttachmentPartner == null ) {
                // Check if there are any nearby lactoses to grab.
                checkForNearbyLactoseToGrab();
            }
        }
        super.setDragging( dragging );
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
            // We are moving towards attachment to a different glucose, so
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

    @Override
    protected void onTransitionToExistingState() {
        setMotionStrategy( new DirectedRandomWalkMotionStrategy( getModel().getInteriorMotionBoundsAboveDna(),
                                                                 INITIAL_DESTINATION_POINT ) );
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
        recoveryFromLactoseAttachmentCountdown = RECOVERY_TIME_FOR_LACTOSE_ATTACHMENT;

        // When the lactose is gone, LacI reverts to its active shape.
        setShape( createActiveConformationShape() );

        // It is now okay for the LacI to fade out of existence.
        setOkayToFade( true );
    }

    private static Shape createActiveConformationShape() {

        // Create the overall outline.
        GeneralPath outline = new GeneralPath();

        outline.moveTo( 0, (float) HEIGHT / 2 );
        outline.quadTo( (float) WIDTH / 2, (float) HEIGHT / 2, (float) WIDTH / 2, -(float) HEIGHT / 2 );
        outline.lineTo( (float) -WIDTH / 2, (float) -HEIGHT / 2 );
        outline.lineTo( (float) -WIDTH / 2, (float) ( HEIGHT * 0.25 ) );
        outline.closePath();
        Area area = new Area( outline );

        // Get the shape of a lactose molecule and shift it to the appropriate
        // position.
        Shape lactoseShape = Lactose.getShape();
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation( 0, HEIGHT / 2 );
        lactoseShape = transform.createTransformedShape( lactoseShape );

        // Get the size of the binding region where this protein will bind to
        // the lac operator and create a shape for it.
        Dimension2D bindingRegionSize = LacOperator.getBindingRegionSize();
        Rectangle2D bindingRegionRect = new Rectangle2D.Double( -bindingRegionSize.getWidth() / 2,
                                                                -HEIGHT / 2, bindingRegionSize.getWidth(), bindingRegionSize.getHeight() );

        // Subtract off the shape of the lactose molecule.
        area.subtract( new Area( lactoseShape ) );

        // Subtract off the shape of the binding region.
        area.subtract( new Area( bindingRegionRect ) );

        return area;
    }

    private static Shape createInactiveConformationShape() {

        // Create the overall outline.
        GeneralPath outline = new GeneralPath();

        outline.moveTo( 0, (float) HEIGHT / 2 );
        outline.quadTo( (float) WIDTH / 2, (float) HEIGHT / 2, (float) WIDTH / 2, -(float) HEIGHT / 3 );
        outline.quadTo( 0, (float) ( -HEIGHT * 0.8 ), (float) -WIDTH / 2, (float) -HEIGHT / 3 );
        outline.lineTo( (float) -WIDTH / 2, (float) ( HEIGHT * 0.25 ) );
        outline.closePath();
        Area area = new Area( outline );

        // Get the shape of a lactose molecule and shift it to the appropriate
        // position.
        Shape lactoseShape = Lactose.getShape();
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation( 0, HEIGHT / 2 );
        lactoseShape = transform.createTransformedShape( lactoseShape );

        // Subtract off the shape of the lactose molecule.
        area.subtract( new Area( lactoseShape ) );

        return area;
    }

    @Override
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        if ( !isUserControlled() ) {

            // Update all internal state that is related to attachments with
            // other model elements.
            updateAttachements( dt );

            // When LacI is first created, it drifts to a location that is
            // close to the lac operator on the DNA strand so that it doesn't
            // have to go too far in order to attach to it.  See if we are in
            // that phase of our existence and, if so, make any updates that
            // are needed.
            Point2D currentDestination = getMotionStrategyRef().getDestinationRef();
            if ( currentDestination != null &&
                 currentDestination.getX() == INITIAL_DESTINATION_POINT.getX() &&
                 currentDestination.getY() == INITIAL_DESTINATION_POINT.getY() ) {

                if ( currentDestination.distance( getPositionRef() ) < 4 ) {
                    // We were moving toward the initial destination and are in
                    // the neighborhood, so we can stop any directed motion and
                    // just hang out here until we are told to do otherwise.
                    getMotionStrategyRef().setDestination( null );
                }
            }
        }
    }

    private void updateAttachements( double dt ) {
        // Update any attachment state related to lac operator first.  Note
        // that it is up to the lac operator to initiate and terminate the
        // attachment, so most of the effort for this relationship happens
        // in that class, not here.
        if ( lacOperatorAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVAILABLE ) {
            if ( recoveryFromLacOperatorAttachmentCountdown != Double.POSITIVE_INFINITY ) {
                recoveryFromLacOperatorAttachmentCountdown -= dt;
                if ( recoveryFromLacOperatorAttachmentCountdown <= 0 ) {
                    // The recovery period is over, we can be available again.
                    // Note that we don't seek out an attachment with lac
                    // operator at this point - it seeks us.
                    lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                }
            }
        }

        // Now update any attachment state related to lactose.  Note that the
        // variable names are actually "glucoseXxx" since that is the molecule
        // to which we try to attach once we verify that it is attached to
        // galactose, thus forming lactose.
        if ( getExistenceState() == ExistenceState.EXISTING ) {

            switch( glucoseAttachmentState ) {

                case UNATTACHED_AND_AVAILABLE:
                    // Look for some lactose to attach to.
                    glucoseAttachmentPartner =
                            getModel().getNearestLactose( getPositionRef(), PositionWrtCell.INSIDE_CELL, true );

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

                            // If we are not moving towards the lac operator, move
                            // towards the lactose.
                            if ( lacOperatorAttachmentPartner == null ) {
                                Dimension2D offsetFromTarget = new PDimension(
                                        Glucose.getLacZAttachmentPointOffset().getWidth() - getGlucoseAttachmentPointOffset().getWidth(),
                                        Glucose.getLacZAttachmentPointOffset().getHeight() - getGlucoseAttachmentPointOffset().getHeight() );

                                setMotionStrategy( new CloseOnMovingTargetMotionStrategy( glucoseAttachmentPartner,
                                                                                          offsetFromTarget, getModel().getInteriorMotionBoundsAboveDna() ) );
                            }
                        }
                    }
                    break;

                case MOVING_TOWARDS_ATTACHMENT:
                    // See if we are close enough to finalize the bond with glucose.
                    if ( getGlucoseAttachmentPointLocation().distance( glucoseAttachmentPartner.getLacZAttachmentPointLocation() ) < ATTACHMENT_FORMING_DISTANCE ) {

                        // Finalize the attachment.
                        completeAttachmentOfGlucose();
                    }
                    break;

                case ATTACHED:
                    // See if conditions are right to detach from the lactose.
                    // This only occurs when there is no other free lactose or
                    // when this LacI is ready to fade out.
                    glucoseAttachmentTimeCountdown -= dt;
                    if ( glucoseAttachmentTimeCountdown <= 0 &&
                         ( getExistenceTimeCountdown() == 0 ||
                           getModel().getNearestLactose( getPositionRef(), PositionWrtCell.INSIDE_CELL, true ) == null ) ) {

                        // Time to release the lactose.
                        glucoseAttachmentPartner.detach( this );
                        glucoseAttachmentPartner = null;
                        glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
                        recoveryFromLactoseAttachmentCountdown = RECOVERY_TIME_FOR_LACTOSE_ATTACHMENT;

                        // When the lactose is gone, LacI reverts to its active shape.
                        setShape( createActiveConformationShape() );

                        // It is now okay for the LacI to fade out of existence.
                        setOkayToFade( true );
                    }

                    /*
                     // See if it's time to end the attachment to glucose.
                     glucoseAttachmentTimeCountdown -= dt;
                     if (glucoseAttachmentTimeCountdown <= 0){
                         // Time to release the lactose.
                         glucoseAttachmentPartner.detach(this);
                         glucoseAttachmentPartner = null;
                         glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
                         recoveryFromLactoseAttachmentCountdown = RECOVERY_TIME_FOR_LACTOSE_ATTACHMENT;

                         // When the lactose is gone, LacI reverts to its active shape.
                         setShape(createActiveConformationShape());

                         // It is now okay for the LacI to fade out of existence.
                         setOkayToFade(true);
                     }
                     */
                    break;

                case UNATTACHED_BUT_UNAVAILABLE:
                    // We are in the recovery phase, so see if we are finished recovering.
                    recoveryFromLactoseAttachmentCountdown -= dt;
                    if ( recoveryFromLactoseAttachmentCountdown <= 0 ) {
                        // Become available again, but only if we are not in the
                        // process of fading out.
                        if ( getExistenceState() == ExistenceState.EXISTING ) {
                            glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                        }
                    }
                    break;
            }
        }
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

    public boolean considerProposalFrom( LacOperator lacOperator ) {
        boolean proposalAccepted = false;

        if ( lacOperatorAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
             getExistenceState() == ExistenceState.EXISTING &&
             glucoseAttachmentPartner == null ) {

            // We can accept the proposal.

            assert lacOperatorAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
            lacOperatorAttachmentPartner = lacOperator;
            lacOperatorAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
            proposalAccepted = true;
            setOkayToFade( false );

            // Set ourself up to move toward the attaching location.
            double xDest = lacOperatorAttachmentPartner.getLacIAttachmentPointLocation().getX() -
                           LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getWidth();
            double yDest = lacOperatorAttachmentPartner.getLacIAttachmentPointLocation().getY() -
                           LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getHeight();
            setMotionStrategy( new DirectedRandomWalkMotionStrategy( getModel().getInteriorMotionBounds(),
                                                                     new Point2D.Double( xDest, yDest ) ) );
            targetPositionForLacOperatorAttachment.setLocation( xDest, yDest );
        }

        return proposalAccepted;
    }

    public boolean isAvailableForAttaching() {
        return ( lacOperatorAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && !isUserControlled() );
    }

    public void attach( LacOperator lacOperator ) {
        if ( lacOperator != lacOperatorAttachmentPartner ) {
            System.err.println( getClass().getName() + " - Error: Finalize request from non-partner." );
            assert false;
            return;
        }
        setMotionStrategy( new StillnessMotionStrategy() );
        setPosition( targetPositionForLacOperatorAttachment );
        lacOperatorAttachmentState = AttachmentState.ATTACHED;
    }

    /**
     * Get the location in absolute space of the attachment point for this type of
     * model element.
     */
    public Point2D getAttachmentPointLocation( LacOperator lacOperator ) {
        return new Point2D.Double( getPositionRef().getX() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getWidth(),
                                   getPositionRef().getY() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getHeight() );
    }

    public static Dimension2D getLacOperatorAttachementPointOffset() {
        return LAC_OPERATOR_ATTACHMENT_POINT_OFFSET;
    }

    public void detach( LacOperator lacOperator ) {
        if ( lacOperator != lacOperatorAttachmentPartner ) {
            System.err.println( getClass().getName() + " - Warning: Request to disconnect received from non-partner." );
            return;
        }

        lacOperatorAttachmentPartner = null;
        lacOperatorAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        recoveryFromLacOperatorAttachmentCountdown = RECOVERY_TIME_FOR_LAC_OPERATOR_ATTACHMENT;

        // It is now okay to fade out if we don't have a relationship with a
        // lactose molecule.
        if ( glucoseAttachmentPartner == null ) {
            setOkayToFade( true );
            setMotionStrategy( new DetachFromDnaThenRandomMotionWalkStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ), 0, null, -1 ) );
        }
        else {
            Dimension2D offsetFromTarget = new PDimension(
                    Glucose.getLacZAttachmentPointOffset().getWidth() - getGlucoseAttachmentPointOffset().getWidth(),
                    Glucose.getLacZAttachmentPointOffset().getHeight() - getGlucoseAttachmentPointOffset().getHeight() );
            setMotionStrategy( new CloseOnMovingTargetMotionStrategy( glucoseAttachmentPartner, offsetFromTarget,
                                                                      getModel().getInteriorMotionBounds() ) );
        }
    }

    /**
     * Complete that process of attaching to glucose.  Created to avoid duplication
     * of code.
     */
    private void completeAttachmentOfGlucose() {

        glucoseAttachmentPartner.attach( this );
        glucoseAttachmentState = AttachmentState.ATTACHED;
        setMotionStrategy( new RandomWalkMotionStrategy( MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
        setShape( createInactiveConformationShape() );

        // If we are currently attached to the lac operator or moving towards
        // attachment with it, detach now, since the basic idea is that when
        // lactose attaches to lacI it prevents it from being able to attach
        // to the lac operator on the DNA strand.
        if ( lacOperatorAttachmentPartner != null ) {
            lacOperatorAttachmentPartner.detach( this );
            lacOperatorAttachmentPartner = null;
            lacOperatorAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
            setMotionStrategy( new DetachFromDnaThenRandomMotionWalkStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ), 0, null, -1 ) );
        }

        // Prevent ourself from fading while bonded.
        setOkayToFade( false );

        // Set the bond time.
        glucoseAttachmentTimeCountdown = LACTOSE_ATTACHMENT_TIME;
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
                                                                          getModel().getInteriorMotionBounds() ) );
            }
        }
    }
}
