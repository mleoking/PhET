// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This class defines a mass in the model that carries with it an associated
 * image that should be presented in the view.  The image can change at times,
 * such as when it is dropped on the balance.
 * <p/>
 * IMPORTANT: All images used by this class are assumed to have their center of
 * mass in the horizontal direction in the center of the image.  In order to
 * make this work for all images, it may be necessary to have some blank
 * transparent space on one side of the image.
 *
 * @author John Blanco
 */
public class ImageMass extends Mass {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that contains the current image.
    final protected Property<BufferedImage> imageProperty;

    // Property that contains the current height of the corresponding model
    // object.  Only height is used, as opposed to both height and width, 
    // because the aspect ratio of the image is expected to be maintained, so
    // the model element's width can be derived from a combination of its
    // height and the the aspect ratio of the image that represents it.  A
    // property is used because the size may change during animations.
    final protected Property<Double> heightProperty = new Property<Double>( 0.0 );

    // Expected duration of the current animation.
    private double expectedAnimationTime = 0;

    // Height at initiation of animation - needed for shrinking.
    private double animationStartHeight;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public ImageMass( double mass, BufferedImage image, double height, Point2D initialPosition ) {
        this( mass, image, height, initialPosition, false );
    }

    /**
     * Constructor.
     */
    public ImageMass( double mass, BufferedImage image, double height, Point2D initialPosition, boolean isMystery ) {
        super( mass, initialPosition, isMystery );
        heightProperty.set( height );
        this.imageProperty = new Property<BufferedImage>( image );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public double getHeight() {
        return heightProperty.get();
    }

    public void addImageChangeObserver( VoidFunction1<BufferedImage> observer ) {
        imageProperty.addObserver( observer );
        observer.apply( imageProperty.get() );
    }


    //REVIEW Why do you need this when positionProperty is public? Or should positionProperty be protected?
    // And positionProperty is a field in the superclass, so why isn't this method in the superclass?
    public void addPositionChangeObserver( VoidFunction1<Point2D> observer ) {
        positionProperty.addObserver( observer );
    }

    public void addHeightChangeObserver( VoidFunction1<Double> observer ) {
        heightProperty.addObserver( observer );
    }

    @Override public Point2D getMiddlePoint() {
        return new Point2D.Double( getPosition().getX(), getPosition().getY() + heightProperty.get() / 2 );
    }

    @Override public void initiateAnimation() {
        // Calculate velocity.  A higher velocity is used if the model element
        // has a long way to travel, otherwise it takes too long.
        double velocity = Math.max( getPosition().distance( animationDestination ) / MAX_REMOVAL_ANIMATION_DURATION, MIN_ANIMATION_VELOCITY );
        expectedAnimationTime = getPosition().distance( animationDestination ) / velocity; // In seconds.
        // Calculate the animation motion vector.
        animationMotionVector.setComponents( velocity, 0 );
        double animationAngle = Math.atan2( animationDestination.getY() - getPosition().getY(), animationDestination.getX() - getPosition().getX() );
        animationMotionVector.rotate( animationAngle );
        // Update the property that tracks the animation state.
        animatingProperty.set( true );
        // Save starting height - needed as a reference.
        animationStartHeight = heightProperty.get();
    }

    @Override public void stepInTime( double dt ) {
        if ( animatingProperty.get() ) {
            if ( getPosition().distance( animationDestination ) < animationMotionVector.getMagnitude() * dt ) {
                // Close enough - animation is complete.
                setPosition( animationDestination );
                animatingProperty.set( false );
                animationScale = 1;
            }
            else {
                // Do a step of the linear animation towards the destination.
                translate( animationMotionVector.getScaledInstance( dt ) );
                // Calculate the scaling factor such that the object is about
                // 10% of its usual size when it reaches the destination.
                animationScale = Math.max( animationScale - ( dt / expectedAnimationTime ) * 0.5, 0.1 );
                heightProperty.set( animationScale * animationStartHeight );
            }
        }
    }

    @Override public Mass clone() {
        ImageMass clone = new ImageMass( this.getMass(), this.imageProperty.get(), this.getHeight(), this.getPosition(), this.isMystery() );
        clone.setCenterOfMassXOffset( getCenterOfMassXOffset() );
        return clone;
    }
}
