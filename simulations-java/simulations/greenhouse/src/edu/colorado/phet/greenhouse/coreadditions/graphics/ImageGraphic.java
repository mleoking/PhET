/**
 * Class: ImageGraphicType
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Oct 19, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.greenhouse.phetcommon.view.util.graphics.ImageLoader;

public class ImageGraphic implements Graphic, ImageObserver, ImageGraphicType {

    private BufferedImage image;
    private Point2D.Double modeLocation;
    private Point2D.Double location = new Point2D.Double();
    private AffineTransform imageTx;
    private AffineTransform orgTx;

    public ImageGraphic( String imageFileLocation, Point2D.Double modelLocation ) {
        this( ImageLoader.fetchBufferedImage( imageFileLocation ), modelLocation );
    }

    public ImageGraphic( BufferedImage image, Point2D.Double modelLocation ) {
        this.image = image;
        this.location = modelLocation;
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }

    /**
     * TODO: add alignment parameters to a constructor that allow x and y to be left, right, top, bottom, or centered
     */

    /**
     * Assumes the incoming Graphics2D has an AffineTransform that transforms model coordinates
     * to view coordinates. This method renders the image in view coordinates.
     *
     * @param g2
     */
    public void paint( Graphics2D g2 ) {

        // Get the transform attached to the Graphics2D so we can get at its attributes
        orgTx = g2.getTransform();
        // Set up the transform that will be applied to the thermometer images
        imageTx = new AffineTransform();
        // Translate the transform to the model origin
        imageTx.translate( orgTx.getTranslateX() / orgTx.getScaleX(), orgTx.getTranslateY() / orgTx.getScaleY() );
        // Concatenate the inverse of the model-to-Swing coords transform
        try {
            imageTx.concatenate( g2.getTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
        // Translate the transform so the bottom of the image is now at the model origin
        imageTx.translate( 0, -image.getHeight() );
        // Move the image to it's location in model coordinates
        imageTx.translate( location.getX() * orgTx.getScaleX(), //- image.getWidth() / 2,
                           location.getY() * orgTx.getScaleY() );

        // Draw the image
        g2.drawImage( image, imageTx, this );
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }

    public boolean contains( Point2D.Double p ) {
        boolean result = false;
        if ( orgTx != null
             && p.getX() >= location.getX() && p.getX() < this.location.getX() + image.getWidth() / orgTx.getScaleX()
             && p.getY() >= location.getY() && p.getY() < this.location.getY() + Math.abs( image.getHeight() / orgTx.getScaleY() ) ) {
            result = true;
        }
        return result;

    }

    public int getRGB( Point2D.Double p ) {
        int result = 0;
        if ( orgTx != null ) {

            // Note the Math.abs() call in determining the upper bound of the image. This may or may not be
            // universally correct
            if ( this.contains( p ) ) {
                Point2D p2 = orgTx.transform( p, null );
                int x = Math.min( image.getWidth() - 1, Math.max( 0, (int) p2.getX() ) );
                int y = Math.min( image.getHeight() - 1, Math.max( 0, (int) p2.getY() ) );
                return getBufferedImage().getRGB( x, y );
            }
        }
        return result;
    }
}

