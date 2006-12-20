/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:39 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.common_cck.view.phetgraphics;

import edu.colorado.phet.common_cck.view.util.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private AffineTransform transform;
    private boolean shapeDirty = true;
    private Shape shape;

    public PhetImageGraphic( Component component, String imageResourceName ) {
        this( component, (BufferedImage)null );

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + imageResourceName );
        }
        setImage( bufferedImage );
    }

    public PhetImageGraphic( Component component, BufferedImage image ) {
        this( component, image, new AffineTransform() );
    }

    public PhetImageGraphic( Component component, BufferedImage image, int x, int y ) {
        super( component );
        this.image = image;
        this.transform = AffineTransform.getTranslateInstance( x, y );
    }

    public PhetImageGraphic( Component component, BufferedImage image, AffineTransform transform ) {
        super( component );
        this.image = image;
        this.transform = transform;
    }

    public Shape getShape() {
        if( shapeDirty ) {
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = transform.createTransformedShape( rect );
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getShape().getBounds();
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            g.drawRenderedImage( image, transform );
        }
    }

    public void setPosition( int x, int y, double scale ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x, y );
        tx.scale( scale, scale );
        setTransform( tx );
    }

    public void setPositionCentered( int x, int y ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x - image.getWidth() / 2, y - image.getHeight() / 2 );
        setTransform( tx );
    }

    public void setPosition( int x, int y ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x, y );
        setTransform( tx );
    }

//    public void setPositionCentered( int x, int y, double scale ) {
//        AffineTransform tx=AffineTransform.getTranslateInstance( x-image.getWidth( )/2,y-image.getHeight( )/2);
//        tx.scale( scale, scale );
//        setTransform( tx );
//    }

    public void setTransform( AffineTransform transform ) {
        if( !transform.equals( this.transform ) ) {
            this.transform = transform;
            setBoundsDirty();
            shapeDirty = true;
            repaint();
        }
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Any side effects produced on this transform will not be automatically
     * observed by this class.  You must call setBoundsDirty(); repaint();
     * or simply setTransform().
     *
     * @return the AffineTransform associated with this PhetImageGraphic.
     */
    public AffineTransform getTransform() {
        return transform;
    }

}
