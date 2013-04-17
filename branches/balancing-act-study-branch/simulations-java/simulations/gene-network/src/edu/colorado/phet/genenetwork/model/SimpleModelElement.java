// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;


/**
 * This is a base class for model elements that exist inside a cell or in
 * extracellular space, and that are not composed of any other model elements.
 * They are, in a sense, the atomic elements of this model.
 *
 * @author John Blanco
 */
public abstract class SimpleModelElement implements IModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Range at which a bond forms when two binding partners are moving
    // towards each other.
    protected static final double ATTACHMENT_FORMING_DISTANCE = 1; // In nanometers.

    // Distance at which a model element that lives on the DNA strand is
    // allowed to jump to it.  Beyond this distance, it is essentially in an
    // illegal location.
    protected static final double LOCK_TO_DNA_DISTANCE = 5; // In nanometers.

    // Rate at which elements fade in and out of existence.  Higher values
    // will make the fading go faster.
    protected static final double FADE_RATE = 0.1;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private Shape shape;
    private Point2D position;
    private Paint paint;  // The paint to use when representing this element in the view.
    private double existenceStrength = 1.0; // Maps to transparency in the view.
    private ExistenceState existenceState = ExistenceState.EXISTING; // Controls whether fading in or out.
    private MutableVector2D velocity = new MutableVector2D();
    protected ArrayList<IModelElementListener> listeners = new ArrayList<IModelElementListener>();
    private AbstractMotionStrategy motionStrategy = null;
    private final IGeneNetworkModelControl model;
    private boolean dragging;
    private double existenceTimeCountdown;
    private double existenceTime;
    private boolean okayToFade = true;
    private Rectangle2D dragBounds = null;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public SimpleModelElement( IGeneNetworkModelControl model, Shape initialShape, Point2D initialPosition,
                               Paint paint, boolean fadeIn, double existenceTime ) {

        this.model = model;
        this.shape = initialShape;
        this.position = initialPosition;
        this.paint = paint;
        this.existenceTime = existenceTime;

        if ( fadeIn ) {
            setExistenceState( ExistenceState.FADING_IN );
            setExistenceStrength( 0.01 );
        }
        else {
            setExistenceState( ExistenceState.EXISTING );
            setExistenceStrength( 1 );
            existenceTimeCountdown = existenceTime;
        }
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public boolean isUserControlled() {
        return dragging;
    }

    public boolean isPartOfDnaStrand() {
        // Assumed not to be part of DNA strand in base class, override as needed.
        return false;
    }

    public Shape getShape() {
        return shape;
    }

    protected IGeneNetworkModelControl getModel() {
        return model;
    }

    /**
     * Set the "okayToFade" state, which (as you might imagine) allows the
     * model element to fade out of existence.  This is generally used to
     * prevent the element from fading away when it is doing something where a
     * fadeout could be awkward or troublesome.
     * <p/>
     * Note that this does NOT pause the existence clock, so it is very
     * possible that clearing this flag will cause this model element to
     * start fading away.
     *
     * @param okayToFade
     */
    protected void setOkayToFade( boolean okayToFade ) {
        this.okayToFade = okayToFade;
    }

    protected void setShape( Shape shape ) {
        this.shape = shape;
        notifyShapeChanged();
    }

    public Point2D getPositionRef() {
        return position;
    }

    /**
     * Get the bounds of this element's shape compensated by the position,
     * which is the bounds in model space.
     *
     * @return
     */
    public Rectangle2D getCompensatedBounds() {
        Rectangle2D shapeBounds = getShape().getBounds2D();
        return new Rectangle2D.Double(
                getPositionRef().getX() + shapeBounds.getMinX(),
                getPositionRef().getY() + shapeBounds.getMinY(),
                shapeBounds.getWidth(),
                shapeBounds.getHeight() );

    }

    public void setPosition( double xPos, double yPos ) {
        if ( xPos != position.getX() || yPos != position.getY() ) {
            if ( !isUserControlled() || dragBounds == null || dragBounds.contains( xPos, yPos ) ) {
                position.setLocation( xPos, yPos );
                notifyPositionChanged();
            }
        }
    }

    public void setPosition( Point2D newPosition ) {
        setPosition( newPosition.getX(), newPosition.getY() );
    }

    /**
     * Set the boundary within which this can be dragged.  This is used to
     * prevent the users from dragging model elements into places that don't
     * make sense or that could create situations that are difficult for the
     * code to handle.
     *
     * @param dragBounds
     */
    protected void setDragBounds( Rectangle2D dragBounds ) {
        this.dragBounds = new Rectangle2D.Double( dragBounds.getX(), dragBounds.getY(), dragBounds.getWidth(),
                                                  dragBounds.getHeight() );
    }

    protected Rectangle2D getDragBounds() {
        return new Rectangle2D.Double( dragBounds.getX(), dragBounds.getY(), dragBounds.getWidth(), dragBounds.getHeight() );
    }

    public Paint getPaint() {
        return paint;
    }

    public String getHtmlLabel() {
        return null;
    }

    public void setVelocity( double xVel, double yVel ) {
        velocity.setComponents( xVel, yVel );
    }

    public void setVelocity( MutableVector2D newVelocity ) {
        setVelocity( newVelocity.getX(), newVelocity.getY() );
    }

    public MutableVector2D getVelocityRef() {
        return velocity;
    }

    public double getExistenceStrength() {
        return existenceStrength;
    }

    protected void setExistenceStrength( double existenceStrength ) {
        if ( this.existenceStrength != existenceStrength ) {
            this.existenceStrength = existenceStrength;
            notifyExistenceStrengthChanged();
        }
    }

    protected ExistenceState getExistenceState() {
        return existenceState;
    }

    private void setExistenceState( ExistenceState existenceState ) {
        this.existenceState = existenceState;
    }

    /**
     * This element is being removed from the model.  Do any cleanup needed
     * and send out notifications.  This should NOT be called by subclasses
     * wanting to remove themselves.  See removeSelfFromModel for that.
     */
    public void removeFromModel() {
        notifyRemovedFromModel();
        listeners.clear();
    }

    protected void notifyPositionChanged() {
        // Notify all listeners of the position change.
        for ( IModelElementListener listener : listeners ) {
            listener.positionChanged();
        }
    }

    /**
     * Remove ourself from the model in which we are contained.  This is done
     * by setting the existence strength to 0, which will lead the model to
     * remove us.
     */
    protected void removeSelfFromModel() {
        setExistenceStrength( 0 );
    }

    private void notifyExistenceStrengthChanged() {
        for ( IModelElementListener listener : listeners ) {
            listener.existenceStrengthChanged();
        }
    }

    private void notifyRemovedFromModel() {
        for ( IModelElementListener listener : listeners ) {
            listener.removedFromModel();
        }
    }

    protected void notifyShapeChanged() {
        // Notify all listeners of the shape change.
        for ( IModelElementListener listener : listeners ) {
            listener.shapeChanged();
        }
    }

    public void addListener( IModelElementListener listener ) {
        if ( listeners.contains( listener ) ) {
            // Don't bother re-adding.
            System.err.println( getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening." );
            assert false;
            return;
        }

        listeners.add( listener );
    }

    public void removeListener( IModelElementListener listener ) {
        listeners.remove( listener );
    }

    public boolean releaseAttachmentWith( IModelElement modelElement ) {
        // Always refuses to release in the base class.
        return false;
    }

    public void stepInTime( double dt ) {
        if ( !isUserControlled() ) {
            if ( motionStrategy != null ) {
                motionStrategy.doUpdatePositionAndMotion( dt, this );
            }
            doFadeInOut( dt );
        }
    }

    private void doFadeInOut( double dt ) {
        switch( getExistenceState() ) {
            case FADING_IN:
                if ( getExistenceStrength() < 1 ) {
                    setExistenceStrength( Math.min( getExistenceStrength() + FADE_RATE, 1 ) );
                }
                else {
                    // Must be fully faded in, so move to next state.
                    setExistenceState( ExistenceState.EXISTING );
                    existenceTimeCountdown = existenceTime;
                    onTransitionToExistingState();
                }
                break;

            case EXISTING:
                if ( existenceTime != Double.POSITIVE_INFINITY ) {
                    existenceTimeCountdown -= dt;
                    if ( existenceTimeCountdown <= 0 ) {
                        // Time to fade out.
                        if ( okayToFade ) {
                            setExistenceState( ExistenceState.FADING_OUT );
                            onTransitionToFadingOutState();
                        }
                        else {
                            // Hold the counter at zero until it is okay to fade.
                            existenceTimeCountdown = 0;
                        }
                    }
                }
                break;

            case FADING_OUT:
                if ( getExistenceStrength() > 0 && okayToFade ) {
                    setExistenceStrength( Math.max( getExistenceStrength() - FADE_RATE, 0 ) );
                }
                // Note: When we get fully faded out, we will be removed from the model.
                break;

            default:
                assert false;
                break;
        }
    }

    protected void setMotionStrategy( AbstractMotionStrategy motionStrategy ) {
        if ( this.motionStrategy != null ) {
            this.motionStrategy.cleanup();
        }
        this.motionStrategy = motionStrategy;
    }

    /**
     * Get the amount of existence time remaining.  This will return zero for
     * the state where it is used up all its existence time but is not allowed
     * to fade out.
     */
    protected double getExistenceTimeCountdown() {
        return existenceTimeCountdown;
    }

    /**
     * This is a hook that allows decendent classes to take some sort of
     * action when transitioning into the EXISTING state.
     */
    protected void onTransitionToExistingState() {
        // Does nothing in base class.
    }

    /**
     * This is a hook that allows decendent classes to take some sort of
     * action when transitioning into the FADING_OUT state.
     */
    protected void onTransitionToFadingOutState() {
        // Does nothing in base class.
    }

    /**
     * Set the time that this model element will exist after fading in (if it
     * fades in) and before fading out.  Generally, this is used in two ways.
     * The first is during object construction to set the time that an object
     * will live in the model before it starts to fade.  The other is after
     * something has occurred to an object whose existence time was previously
     * infinite, and that now needs to fade out.  In the second case, setting
     * the time to zero will initiate an immediate fade out.
     * <p/>
     * Note that if the existence time had started counting down, calling this
     * method will effectively reset it to the new value.
     * <p/>
     * Also note that calling this at a time when the object had already
     * started fading out would have no effect.
     *
     * @param existenceTime
     */
    protected void setExistenceTime( double existenceTime ) {
        if ( existenceState == ExistenceState.FADING_OUT ) {
            System.err.println( getClass().getName() + " - Warning: setExistenceTime called when already fading, this will have no effect." );
        }
        else {
            this.existenceTime = existenceTime;
            existenceTimeCountdown = existenceTime;
        }
    }

    protected AbstractMotionStrategy getMotionStrategyRef() {
        return motionStrategy;
    }

    /**
     * Set the state that indicates whether or not the user is dragging this
     * model element.
     */
    public void setDragging( boolean dragging ) {
        if ( this.dragging != dragging ) {
            this.dragging = dragging;
            if ( dragging == false ) {
                if ( model.isPointInToolBox( getPositionRef() ) || !isInAllowableLocation() ) {
                    // This model element is being released by the user in a location
                    // that is either inside the tool box or is in a disallowed
                    // location, so remove it from the model.
                    removeSelfFromModel();
                }
                else {
                    // The element is being released by the user outside the
                    // toolbox.  See if it needs to be moved to any particular
                    // location.
                    if ( isPartOfDnaStrand() ) {
                        // This element is part of the DNA strand, so move it to
                        // the correct location with the strand.
                        setPosition( getDefaultLocation() );
                    }
                }
            }
        }
    }

    /**
     * Returns a value indicating whether the model element is in a
     * "allowable location".  This is generally intended to be overridden
     * by subclasses that should only be in certain places within the model,
     * such as those that reside on the DNA strand.
     */
    protected boolean isInAllowableLocation() {
        return true;
    }

    /**
     * Get the location where this model element should reside within the
     * model.  This should be overridden for each element that has a default
     * location, such as those that reside on the DNA strand.  Some elements
     * will not have a default location, and for them this should not be
     * overridden.
     */
    protected Point2D getDefaultLocation() {
        assert false; // Should never be invoked in the base class, and yet
        // not all element will necessarily need to override
        // this method, so just make sure it gets noticed if
        // we end up here.
        return new Point2D.Double( 0, 0 );
    }

    protected enum ExistenceState {FADING_IN, EXISTING, FADING_OUT}

    ;
}
