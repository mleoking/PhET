// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This class defines a mass in the model that carries with it an associated
 * image that should be presented in the view.  The image can change at times,
 * such as when it is dropped on the balance.
 * <p/>
 * IMPORTANT: All images used by this class are assumed to have their center
 * of mass in the horizontal direction in the center of the image.  In order
 * to make this work for all images, it may be necessary to have some blank
 * transparent space on one side of the image.
 *
 * @author John Blanco
 */
public class ImageMass extends Mass {

    // Property that contains the current image.
    final protected Property<BufferedImage> imageProperty;

    // Property that contains the position in model space.  By convention for
    // this simulation, the position of a mass is the center bottom of the
    // model object.
    final public Property<Point2D> positionProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // Current height of the corresponding model object.  Only height is used,
    // as opposed to both height and width, because the aspect ratio of the
    // image is expected to be maintained, so the model element's width can be
    // derived from a combination of its height and the the aspect ratio of
    // the image that represents it.
    protected double height;

    /**
     * Constructor.
     */
    public ImageMass( double mass, BufferedImage image, double height, Point2D initialPosition ) {
        super( mass );
        this.height = height;
        positionProperty.set( initialPosition );
        this.imageProperty = new Property<BufferedImage>( image );
    }

    public double getHeight() {
        return height;
    }

    public void addImageChangeObserver( VoidFunction1<BufferedImage> observer ) {
        imageProperty.addObserver( observer );
        observer.apply( imageProperty.get() );
    }

    public void addPositionChangeObserver( VoidFunction1<Point2D> observer ) {
        positionProperty.addObserver( observer );
    }

    /**
     * Set the position, which is the location of the bottom center of mass.
     */
    public void setPosition( double x, double y ) {
        positionProperty.set( new Point2D.Double( x, y ) );
    }

    /**
     * Set the position, which is the location of the bottom center of mass.
     */
    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }

    public void addRotationalAngleChangeObserver( VoidFunction1<Double> changeObserver ) {
        rotationalAngleProperty.addObserver( changeObserver );
    }

    @Override public void translate( double x, double y ) {
        setPosition( positionProperty.get().getX() + x, positionProperty.get().getY() + y );
    }

    @Override public void translate( ImmutableVector2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    @Override public Point2D getMiddlePoint() {
        return new Point2D.Double( getPosition().getX(), getPosition().getY() + height / 2 );
    }

    @Override public void initiateAnimation() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Point2D getPosition() {
        return new Point2D.Double( positionProperty.get().getX(), positionProperty.get().getY() );
    }

    protected void setNewImageAndHeight( double height, BufferedImage image ) {
        // Update height first so that listeners to the image property will
        // be able to scale properly.
        this.height = height;
        imageProperty.set( image );
    }
}
