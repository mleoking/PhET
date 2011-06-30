// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This class defines a weight in the model that carries with it an associated
 * image that should be presented in the view.  The image can change at times,
 * such as when it is dropped on the balance.
 *
 * @author John Blanco
 */
public class ImageWeight {

    // Property that contains the current image.
    final private Property<BufferedImage> imageProperty;

    // Property the contains the position in model space.  By convention for
    // this simulation, the position of a weight is the center bottom of the
    // model object.
    final private Property<Point2D> position = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // Current height of the corresponding model object.  Only height is used,
    // as opposed to both height and width, because the aspect ratio of the
    // image is expected to be maintained, so the model element's width can be
    // derived from a combination of its height and the the aspect ratio of
    // the image that represents it.
    private double height;

    /**
     * Constructor.
     */
    public ImageWeight( BufferedImage image, double height, Point2D initialPosition ) {
        this.height = height;
        position.set( initialPosition );
        this.imageProperty = new Property<BufferedImage>( image );
    }

    public double getHeight() {
        return height;
    }

    public void addImageChangeObserver( VoidFunction1<BufferedImage> observer ) {
        imageProperty.addObserver( observer );
    }

    public void addPositionChangeObserver( VoidFunction1<Point2D> observer ) {
        position.addObserver( observer );
    }

    public void setPosition( double x, double y ) {
        position.set( new Point2D.Double( x, y ) );
    }

    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }

    public Point2D getPosition() {
        return new Point2D.Double( position.get().getX(), position.get().getY() );
    }

    protected void setNewImageAndHeight( double height, BufferedImage image ) {
        // Update height first so that listeners to the image property will
        // be able to scale properly.
        this.height = height;
        imageProperty.set( image );
    }
}
