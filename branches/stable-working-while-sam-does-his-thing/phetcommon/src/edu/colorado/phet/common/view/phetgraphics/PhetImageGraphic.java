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
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    // Affine transform relative to the image's origin
    private AffineTransform relativeTx;
    // Affine transform relative to the image's location
    private AffineTransform absoluteTx;
    private AffineTransformOp atxOp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR );
    private boolean shapeDirty = true;
    private Shape shape;
    private Point2D location = new Point2D.Double();

    public PhetImageGraphic( Component component ) {
        super( component );
    }

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
        this.relativeTx = AffineTransform.getTranslateInstance( x, y );
    }

    public PhetImageGraphic( Component component, BufferedImage image, AffineTransform transform ) {
        super( component );
        this.image = image;
        this.relativeTx = transform;
    }

    public Shape getShape() {
        if( shapeDirty ) {
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = absoluteTx.createTransformedShape( rect );
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        if( this.image == null ) {
            return null;
        }
        else {
            return getShape().getBounds();
        }
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            g.drawImage( image, atxOp, (int)location.getX(), (int)location.getY() );
        }
    }

    public void setPosition( int x, int y, double scale ) {
        setPosition( x, y );
        AffineTransform tx = AffineTransform.getScaleInstance( scale, scale );
        setTransform( tx );
    }

    public void setPositionCentered( int x, int y ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x - image.getWidth() / 2, y - image.getHeight() / 2 );
        setTransform( tx );
    }

    public void setPosition( int x, int y ) {
        location.setLocation( x, y );
        setAbsoluteTx();
    }

//    public void setPositionCentered( int x, int y, double scale ) {
//        AffineTransform tx=AffineTransform.getTranslateInstance( x-image.getWidth( )/2,y-image.getHeight( )/2);
//        tx.scale( scale, scale );
//        setTransform( tx );
//    }

    /**
     * @param relativeTransform An affine relativeTx that is relative to the
     *                          images origin
     */
    public void setTransform( AffineTransform relativeTransform ) {
        if( !relativeTransform.equals( this.relativeTx ) ) {
            this.relativeTx = relativeTransform;
            setAbsoluteTx();
            atxOp = new AffineTransformOp( relativeTransform, AffineTransformOp.TYPE_BILINEAR );
            setBoundsDirty();
            shapeDirty = true;
            repaint();
        }
    }

    private void setAbsoluteTx() {
        absoluteTx = new AffineTransform( getTransform() );
        absoluteTx.preConcatenate( AffineTransform.getTranslateInstance( location.getX(), location.getY() ) );
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Any side effects produced on this relativeTx will not be automatically
     * observed by this class.  You must call setBoundsDirty(); repaint();
     * or simply setTransform().
     *
     * @return the AffineTransform associated with this PhetImageGraphic.
     */
    public AffineTransform getTransform() {
        return relativeTx;
    }

}
