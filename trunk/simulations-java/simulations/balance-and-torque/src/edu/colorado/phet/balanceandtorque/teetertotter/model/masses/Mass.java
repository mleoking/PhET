// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for all objects that can be placed on the balance.
 *
 * @author John Blanco
 */
public abstract class Mass implements UserMovableModelElement {


    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    protected static final double MIN_ANIMATION_VELOCITY = 3; // In meters/sec.
    protected static final double MAX_REMOVAL_ANIMATION_DURATION = 0.75; // In seconds.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that indicates whether this mass is currently user controlled,
    // i.e. being moved around by the user.
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // The mass of this...mass.  Yes, it is the same word used as two
    // different linguistic constructs, but the physicists said that the term
    // "weight" was not appropriate, so we're stuck with this situation.
    private final double mass;

    // Property that contains the position in model space.  By convention for
    // this simulation, the position of a mass is the center bottom of the
    // model object.
    final public Property<Point2D> positionProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // Property that contains the rotational angle, in radians, of the model
    // element.  By convention for this simulation, the point of rotation is
    // considered to be the center bottom of the model element.
    final protected Property<Double> rotationalAngleProperty = new Property<Double>( 0.0 );

    // Boolean property that indicates whether this model element is currently
    // animating.  At the time of this writing, the only animation supported
    // is a simple linear motion to a preset point.
    final protected BooleanProperty animatingProperty = new BooleanProperty( false );

    // Since not all objects are symmetrical, some may need to have an offset
    // that indicates where their center of mass is when placed on a balance.
    // This is the horizontal offset from the center of the shape or image.
    private double centerOfMassXOffset = 0;

    // Destination of linear animation.
    protected Point2D animationDestination = new Point2D.Double();
    // Vector that describes the amount of linear motion for one time step.
    final protected Vector2D animationMotionVector = new Vector2D( 0, 0 );
    // Scale factor, used primarily during animation.
    protected double animationScale = 1;
    // Expected duration of an in-progress animation.
    protected double expectedAnimationTime = 0;

    // Flag that indicates whether this mass should be a "mystery", meaning
    // that certain visual indications should be hidden from the user.
    private final boolean isMystery;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Mass( double mass ) {
        this( mass, false );
    }

    public Mass( double mass, boolean isMystery ) {
        this.mass = mass;
        this.isMystery = isMystery;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public double getMass() {
        return mass;
    }

    /**
     * Set the position, which is the location of the bottom center of mass.
     */
    public void setPosition( double x, double y ) {
        positionProperty.set( new Point2D.Double( x, y ) );
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public Point2D getPosition() {
        return new Point2D.Double( positionProperty.get().getX(), positionProperty.get().getY() );
    }

    ;

    public void translate( double x, double y ) {
        positionProperty.set( new Point2D.Double( positionProperty.get().getX() + x, positionProperty.get().getY() + y ) );
    }

    ;

    public void translate( ImmutableVector2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    ;

    public void addPositionChangeObserver( VoidFunction1<Point2D> changeObserver ) {
        positionProperty.addObserver( changeObserver );
    }

    public void removePositionChangeObserver( VoidFunction1<Point2D> changeObserver ) {
        positionProperty.removeObserver( changeObserver );
    }

    public abstract Point2D getMiddlePoint();

    public double getCenterOfMassXOffset() {
        return centerOfMassXOffset;
    }

    public void setCenterOfMassXOffset( double centerOfMassXOffset ) {
        this.centerOfMassXOffset = centerOfMassXOffset;
    }

    /**
     * Initiate this element's animation to the animation destination point.
     * This consists of moving the element in a stepwise fashion back to the
     * point where it was originally added to the model while simultaneously
     * reducing its size, and then signaling that the animation is complete. At
     * that point, it is generally removed from the model.
     */
    public void initiateAnimation() {
        // In the default implementation, the signal is sent that says that
        // the animation is complete, but no actual animation is done.
        // Override to implement the subclass-specific animation.
        animatingProperty.set( true );
        animatingProperty.set( false );
    }

    public void addAnimationStateObserver( ChangeObserver<Boolean> changeObserver ) {
        animatingProperty.addObserver( changeObserver );
    }

    public void removeAnimationStateObserver( ChangeObserver<Boolean> changeObserver ) {
        animatingProperty.removeObserver( changeObserver );
    }

    public void setOnPlank( boolean onPlank ) {
        // Handle any changes that need to happen when added to the plank,
        // such as changes to shape or image.  By default, this does nothing.
    }

    /**
     * Set the angle of rotation.  The point of rotation is the position handle.
     * For a mass, that means that this method can be used to make it appear to
     * sit will on plank.
     *
     * @param angle rotational angle in radians.
     */
    public void setRotationAngle( double angle ) {
        rotationalAngleProperty.set( angle );
    }

    public double getRotationAngle() {
        return rotationalAngleProperty.get();
    }

    public void addRotationalAngleChangeObserver( VoidFunction1<Double> changeObserver ) {
        rotationalAngleProperty.addObserver( changeObserver );
    }

    public void removeRotationalAngleChangeObserver( VoidFunction1<Double> changeObserver ) {
        rotationalAngleProperty.removeObserver( changeObserver );
    }

    /**
     * The user has released this mass.
     */
    public void release() {
        userControlled.set( false );
    }

    public void setAnimationDestination( double x, double y ) {
        animationDestination.setLocation( x, y );
    }

    public void setAnimationDestination( Point2D animationDestination ) {
        setAnimationDestination( animationDestination.getX(), animationDestination.getY() );
    }

    /**
     * Implements any time-dependent behavior of the mass.
     *
     * @param dt - Time change since last call.
     */
    public void stepInTime( double dt ) {
        // Default implementation does nothing.
    }
}
