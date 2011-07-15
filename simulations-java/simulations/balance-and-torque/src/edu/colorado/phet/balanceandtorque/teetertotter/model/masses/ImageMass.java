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
 *
 * @author John Blanco
 */
public abstract class ImageMass extends Mass {

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

    public void setPosition( double x, double y ) {
        positionProperty.set( new Point2D.Double( x, y ) );
    }

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
