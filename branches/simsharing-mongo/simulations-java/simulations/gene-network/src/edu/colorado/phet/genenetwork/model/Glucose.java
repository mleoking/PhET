// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final Dimension2D ATTACHMENT_POINT_OFFSET = new PDimension( getWidth() / 2, 0 );
    private static final double HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT = 2; // In seconds.
    private static final double POST_ATTACHMENT_RECOVERY_TIME = 1; // In seconds.

    // If this molecule is moved by the user to within this distance from a
    // LacI, it should try to attach to it.
    private static final double LAC_I_IMMEDIATE_ATTACH_DISTANCE = 8;

    // If this molecule is moved by the user to within this distance from a
    // LacZ, it should try to attach to it.
    private static final double LAC_Z_IMMEDIATE_ATTACH_DISTANCE = 8;

    // If this molecule is moved by the user to within this distance from a
    // LacY, it should try to attach to it.
    private static final double LAC_Y_IMMEDIATE_ATTACH_DISTANCE = 8;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private Galactose galactoseAttachmentPartner; // Partner for combining to create lactose.
    private LacZ lacZAttachmentPartner;
    private AttachmentState lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
    private LacI lacIAttachmentPartner;
    private AttachmentState lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
    private LacY lacYAttachmentPartner;
    private AttachmentState lacYAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
    private double holdoffPriorToFirstAttachmentCountdown = HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT;
    private double postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public Glucose( IGeneNetworkModelControl model, Point2D initialPosition ) {
        super( model, initialPosition, Color.BLUE );
    }

    public Glucose( IGeneNetworkModelControl model ) {
        this( model, new Point2D.Double() );
    }

    public Glucose() {
        this( null );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public static Dimension2D getGalactoseAttachmentPointOffset() {
        return new PDimension( ATTACHMENT_POINT_OFFSET );
    }

    public static Dimension2D getLacZAttachmentPointOffset() {
        return new PDimension( ATTACHMENT_POINT_OFFSET );
    }

    public static Dimension2D getLacIAttachmentPointOffset() {
        return new PDimension( ATTACHMENT_POINT_OFFSET );
    }

    public void formLactose( Galactose galactose ) {
        assert galactoseAttachmentPartner == null; // Should not be requested to attach if already attached.

        galactoseAttachmentPartner = galactose;
        galactoseAttachmentPartner.attach( this );  // This will move galactose to the appropriate location.
    }

    public boolean isAvailableForAttaching() {
        return ( lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
                 lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
                 lacYAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE );
    }

    public boolean isBoundToGalactose() {
        return !( galactoseAttachmentPartner == null );
    }

    public boolean considerProposalFrom( LacZ lacZ ) {
        boolean proposalAccepted = false;

        if ( lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
             getExistenceState() == ExistenceState.EXISTING ) {

            assert lacZAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
            lacZAttachmentPartner = lacZ;
            lacZAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
            proposalAccepted = true;

            // Set ourself up to move toward the attaching location.
            Dimension2D offsetFromTarget = new PDimension(
                    LacZ.getGlucoseAttachmentPointOffset().getWidth() - getLacZAttachmentPointOffset().getWidth(),
                    LacZ.getGlucoseAttachmentPointOffset().getHeight() - getLacZAttachmentPointOffset().getHeight() );
            setMotionStrategy( new CloseOnMovingTargetMotionStrategy( lacZ, offsetFromTarget,
                                                                      MotionBoundsTrimmer.trim( getModel().getInteriorMotionBounds(), this ) ) );
        }

        return proposalAccepted;
    }

    public boolean considerProposalFrom( LacI lacI ) {
        boolean proposalAccepted = false;

        if ( lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
             getExistenceState() == ExistenceState.EXISTING ) {

            assert lacIAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
            lacIAttachmentPartner = lacI;
            lacIAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
            proposalAccepted = true;

            // Set ourself up to move toward the attaching location.
            Dimension2D offsetFromTarget = new PDimension(
                    LacI.getGlucoseAttachmentPointOffset().getWidth() - getLacIAttachmentPointOffset().getWidth(),
                    LacI.getGlucoseAttachmentPointOffset().getHeight() - getLacIAttachmentPointOffset().getHeight() );
            setMotionStrategy( new CloseOnMovingTargetMotionStrategy( lacI, offsetFromTarget,
                                                                      MotionBoundsTrimmer.trim( getModel().getInteriorMotionBounds(), this ) ) );
        }

        return proposalAccepted;
    }

    public boolean considerProposalFrom( LacY lacY ) {
        boolean proposalAccepted = false;

        if ( lacYAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
             getExistenceState() == ExistenceState.EXISTING ) {

            assert lacYAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
            lacYAttachmentPartner = lacY;
            lacYAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
            proposalAccepted = true;

            // Set ourself up to move toward the attaching location.
            setMotionStrategy( new DirectedRandomWalkMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getExteriorMotionBounds(), this ),
                    lacY.getGlucoseAttachmentPointLocation() ) );
        }

        return proposalAccepted;
    }

    public void attach( LacZ lacZ ) {
        if ( lacZ != lacZAttachmentPartner ) {
            // For this bond, it is expected that we were already moving
            // towards this partner.  If not, it's unexpected.
            System.err.println( getClass().getName() + " - Error: Attach request from non-partner." );
            assert false;
            return;
        }
        setPosition( lacZ.getGlucoseAttachmentPointLocation().getX() - ATTACHMENT_POINT_OFFSET.getWidth(),
                     lacZ.getGlucoseAttachmentPointLocation().getY() - ATTACHMENT_POINT_OFFSET.getHeight() );
        Dimension2D followingOffset = new PDimension(
                LacZ.getGlucoseAttachmentPointOffset().getWidth() - ATTACHMENT_POINT_OFFSET.getWidth(),
                LacZ.getGlucoseAttachmentPointOffset().getHeight() - ATTACHMENT_POINT_OFFSET.getHeight() );
        setMotionStrategy( new FollowTheLeaderMotionStrategy( this, lacZ, followingOffset ) );
        lacZAttachmentState = AttachmentState.ATTACHED;
    }

    public void attach( LacI lacI ) {
        if ( lacI != lacIAttachmentPartner ) {
            // For this bond, it is expected that we were already moving
            // towards this partner.  If not, it's unexpected.
            System.err.println( getClass().getName() + " - Error: Attach request from non-partner." );
            assert false;
            return;
        }
        setPosition( lacI.getGlucoseAttachmentPointLocation().getX() - ATTACHMENT_POINT_OFFSET.getWidth(),
                     lacI.getGlucoseAttachmentPointLocation().getY() - ATTACHMENT_POINT_OFFSET.getHeight() );
        Dimension2D followingOffset = new PDimension(
                LacI.getGlucoseAttachmentPointOffset().getWidth() - ATTACHMENT_POINT_OFFSET.getWidth(),
                LacI.getGlucoseAttachmentPointOffset().getHeight() - ATTACHMENT_POINT_OFFSET.getHeight() );
        setMotionStrategy( new FollowTheLeaderMotionStrategy( this, lacI, followingOffset ) );
        lacIAttachmentState = AttachmentState.ATTACHED;
    }

    public void attach( LacY lacY ) {
        if ( lacY != lacYAttachmentPartner ) {
            // For this bond, it is expected that we were already moving
            // towards this partner.  If not, it's unexpected.
            System.err.println( getClass().getName() + " - Error: Attach request from non-partner." );
            assert false;
            return;
        }
        setPosition( lacY.getGlucoseAttachmentPointLocation().getX() - ATTACHMENT_POINT_OFFSET.getWidth(),
                     lacY.getGlucoseAttachmentPointLocation().getY() - ATTACHMENT_POINT_OFFSET.getHeight() );
        setMotionStrategy( new StillnessMotionStrategy() );
        lacYAttachmentState = AttachmentState.ATTACHED;
    }

    /**
     * Detach from LacZ.  This is intended to be used by a LacZ instance that wants
     * to detach.
     *
     * @param lacI
     */
    public void detach( LacZ lacZ ) {
        assert lacZ == lacZAttachmentPartner;
        lacZAttachmentPartner = null;
        setMotionStrategy( new LinearThenRandomMotionStrategy(
                MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ),
                getPositionRef(), new Vector2D( -3, -8 ), 1 ) );

        if ( galactoseAttachmentPartner == null ) {
            // This glucose molecule is not part of a lactose molecule, so it
            // has essentially be "digested".  It shouldn't be available for
            // any attachments.
            lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
        }
        else {
            // This glucose molecule is still part of a lactose molecule, so
            // it should be made available for attachment.
            lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
        }
    }

    /**
     * Detach from LacI.  This is intended to be used by a LacI instance that wants
     * to detach.
     *
     * @param lacI
     */
    public void detach( LacI lacI ) {
        assert lacI == lacIAttachmentPartner;
        lacIAttachmentPartner = null;
        if ( lacIAttachmentState == AttachmentState.ATTACHED ) {
            // Move up and away, then get random.
            setMotionStrategy( new LinearThenRandomMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ),
                    getPositionRef(), new Vector2D( 0, 8 ), 0.5 ) );
            // Recover for a while before attaching to something else.
            lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
            postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;
        }
        else {
            // Go back to random motion.
            setMotionStrategy( new RandomWalkMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            // Okay to attach to something else right away.
            lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            postAttachmentRecoveryCountdown = 0;
        }
    }

    /**
     * Detach from LacY.  This is intended to be used by a LacY instance that wants
     * to detach.
     *
     * @param lacY
     */
    public void detach( LacY lacY ) {
        assert lacY == lacYAttachmentPartner;
        lacYAttachmentPartner = null;
        if ( lacYAttachmentState == AttachmentState.ATTACHED ) {
            // Move down and away, then get random.
            setMotionStrategy( new LinearThenRandomMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ),
                    getPositionRef(), new Vector2D( 0, -8 ), 0.5 ) );
            // Recover for a while before attaching to something else.
            lacYAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
            postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;
        }
        else {
            // Go back to random motion.
            setMotionStrategy( new RandomWalkMotionStrategy(
                    MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            // Okay to attach to something else right away.
            lacYAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            postAttachmentRecoveryCountdown = 0;
        }
    }

    /**
     * This is called to force this molecule to release the attached galactose
     * molecule, essentially breaking down from lactose into the constituent
     * molecules.
     */
    public void releaseGalactose() {
        if ( galactoseAttachmentPartner == null ) {
            System.err.println( getClass().getName() + " - Error: Told to detach galactose when not attached." );
            return;
        }
        galactoseAttachmentPartner.detach( this );
        galactoseAttachmentPartner = null;

        // Once broken down from being a part of lactose, this fades away.
        setExistenceTime( 0.5 );
    }

    /**
     * Get the location in absolute model space of this element's attachment point
     * for LacZ.
     */
    public Point2D getLacZAttachmentPointLocation() {
        return ( new Point2D.Double( getPositionRef().getX() + ATTACHMENT_POINT_OFFSET.getWidth(),
                                     getPositionRef().getY() + ATTACHMENT_POINT_OFFSET.getHeight() ) );
    }

    /**
     * Get the location in absolute model space of this element's attachment point
     * for LacY.
     */
    public Point2D getLacYAttachmentPointLocation() {
        return ( new Point2D.Double( getPositionRef().getX() + ATTACHMENT_POINT_OFFSET.getWidth(),
                                     getPositionRef().getY() + ATTACHMENT_POINT_OFFSET.getHeight() ) );
    }

    @Override
    public void stepInTime( double dt ) {
        if ( holdoffPriorToFirstAttachmentCountdown > 0 ) {
            holdoffPriorToFirstAttachmentCountdown -= dt;
            if ( holdoffPriorToFirstAttachmentCountdown <= 0 ) {
                lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                lacYAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        else if ( ( lacIAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVAILABLE ||
                    lacYAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVAILABLE ) &&
                  !isUserControlled() ) {

            postAttachmentRecoveryCountdown -= dt;
            if ( postAttachmentRecoveryCountdown <= 0 ) {
                // Recovery complete - we are ready to attach again.
                lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
                lacYAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        super.stepInTime( dt );
    }

    @Override
    public void setDragging( boolean dragging ) {
        if ( dragging == true ) {
            // The user has grabbed this node and is moving it.  Is it
            // attached to a LacI, LacZ, or LacY?
            if ( lacIAttachmentPartner != null ) {
                // It is attached to a LacI, so it needs to detach.
                assert lacIAttachmentState == AttachmentState.ATTACHED || lacIAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
                lacIAttachmentPartner.detach( this );
                lacIAttachmentPartner = null;
                setMotionStrategy( new RandomWalkMotionStrategy(
                        MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            }
            else if ( lacZAttachmentPartner != null ) {
                // It is attached to a LacZ, so it needs to detach.
                assert lacZAttachmentState == AttachmentState.ATTACHED || lacZAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
                lacZAttachmentPartner.detach( this );
                lacZAttachmentPartner = null;
                setMotionStrategy( new RandomWalkMotionStrategy(
                        MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), this ) ) );
            }
            else if ( lacYAttachmentPartner != null ) {
                // It is attached to a LacY, so it needs to detach.
                assert lacYAttachmentState == AttachmentState.ATTACHED || lacYAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
                lacYAttachmentPartner.detach( this );
                lacYAttachmentPartner = null;
                setMotionStrategy( new RandomWalkMotionStrategy( MotionBoundsTrimmer.trim( getModel().getExteriorMotionBounds(), this ) ) );
            }
            // Make it unavailable for other attachments.
            lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
            lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;
            lacYAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVAILABLE;

            // If the initial holdoff timer is running, stop it.
            if ( holdoffPriorToFirstAttachmentCountdown > 0 ) {
                holdoffPriorToFirstAttachmentCountdown = 0;
            }
        }
        else {
            // This element has just been released by the user.  It should be
            // considered available.
            lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            lacYAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            assert lacIAttachmentPartner == null;
            assert lacZAttachmentPartner == null;
            assert lacYAttachmentPartner == null;

            if ( getModel().classifyPosWrtCell( getPositionRef() ) == PositionWrtCell.INSIDE_CELL ) {
                // If this was dropped close to a LacI, it should try to
                // attach to it.
                ArrayList<LacI> lacIList = getModel().getLacIList();
                for ( LacI lacI : lacIList ) {
                    if ( lacI.getPositionRef().distance( getPositionRef() ) < LAC_I_IMMEDIATE_ATTACH_DISTANCE ) {
                        if ( lacI.requestImmediateAttach( this ) ) {
                            // The request was accepted, so our work here is done.
                            // Note that all of the setting of state variables
                            // related to this attachment will be done as a result
                            // of requesting the immediate attachment, so nothing
                            // else needs to be done here.
                            break;
                        }
                    }
                }
                if ( lacIAttachmentState != AttachmentState.ATTACHED &&
                     lacIAttachmentState != AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {

                    // If we were dropped close to a LacZ, and we weren't already
                    // picked up by a lacI, we should attach to the LacZ.
                    ArrayList<LacZ> lacZList = getModel().getLacZList();
                    for ( LacZ lacZ : lacZList ) {
                        if ( lacZ.getPositionRef().distance( getPositionRef() ) < LAC_Z_IMMEDIATE_ATTACH_DISTANCE ) {
                            if ( lacZ.requestImmediateAttach( this ) ) {
                                // The request was accepted, so our work here is done.
                                // Note that all of the setting of state variables
                                // related to this attachment will be done as a result
                                // of requesting the immediate attachment, so nothing
                                // else needs to be done here.
                                break;
                            }
                        }
                    }
                }
            }
            else {
                // If this was dropped close to a LacY, and is outside the
                // cell, it should try to attach.
                ArrayList<LacY> lacYList = getModel().getLacYList();
                for ( LacY lacY : lacYList ) {
                    if ( lacY.getPositionRef().distance( getPositionRef() ) < LAC_Y_IMMEDIATE_ATTACH_DISTANCE ) {
                        if ( lacY.requestImmediateAttach( this ) ) {
                            // The request was accepted, so our work here is done.
                            // Note that all of the setting of state variables
                            // related to this attachment will be done as a result
                            // of requesting the immediate attachment, so nothing
                            // else needs to be done here.
                            break;
                        }
                    }
                }
            }
        }

        super.setDragging( dragging );
    }

    /**
     * Break off any pending attachments, i.e. attachments that have yet to be
     * finalized.  This is generally used when a model element has identified this
     * as something that it wants to attach to.  It's like someone is engaged to be
     * married and someone else comes along and says, "Hey, marry me instead".
     *
     * @return - true if able to break off or if it was already available, false
     *         otherwise.
     */
    public boolean breakOffPendingAttachments( SimpleModelElement modelElement ) {
        if ( isAvailableForAttaching() ) {
            // Already available, so bail now.
            return true;
        }

        if ( lacZAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
            // Only do this if the requester is closer than the current pending partner.
            if ( getPositionRef().distance( modelElement.getPositionRef() ) < getPositionRef().distance( lacZAttachmentPartner.getPositionRef() ) ) {
                lacZAttachmentPartner.detach( this );
                lacZAttachmentPartner = null;
                lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        if ( lacIAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
            // Only do this if the requester is closer than the current pending partner.
            if ( getPositionRef().distance( modelElement.getPositionRef() ) < getPositionRef().distance( lacIAttachmentPartner.getPositionRef() ) ) {
                lacIAttachmentPartner.detach( this );
                lacIAttachmentPartner = null;
                lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }
        if ( lacYAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT ) {
            // Only do this if the requester is closer than the current pending partner.
            if ( getPositionRef().distance( modelElement.getPositionRef() ) < getPositionRef().distance( lacYAttachmentPartner.getPositionRef() ) ) {
                lacYAttachmentPartner.detach( this );
                lacYAttachmentPartner = null;
                lacYAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
            }
        }

        return isAvailableForAttaching();
    }

    /**
     * Set up the draggable bounds based on either being outside or inside the
     * cell.  This is needed for glucose since, at least as of this writing (April
     * 4, 2010), lactose is the only thing that crosses the cell membrane.
     *
     * @param positionWrtCell
     */
    public void setUpDraggableBounds( PositionWrtCell positionWrtCell ) {

        switch( positionWrtCell ) {

            case INSIDE_CELL:
                setDragBounds( getModel().getInteriorMotionBounds() );
                if ( galactoseAttachmentPartner != null ) {
                    galactoseAttachmentPartner.setDragBounds( getModel().getInteriorMotionBounds() );
                }
                break;

            case OUTSIDE_CELL:
                setDragBounds( getModel().getExteriorMotionBounds() );
                if ( galactoseAttachmentPartner != null ) {
                    galactoseAttachmentPartner.setDragBounds( getModel().getExteriorMotionBounds() );
                }
                break;

            default:
                assert false;
                break;
        }
    }
}
