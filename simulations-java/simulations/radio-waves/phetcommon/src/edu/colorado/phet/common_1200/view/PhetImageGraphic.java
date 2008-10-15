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
package edu.colorado.phet.common_1200.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private AffineTransform transform;
    private boolean shapeDirty = true;
    private Shape shape;

    public PhetImageGraphic( Component component, BufferedImage image ) {
        super( component );
        this.image = image;
        this.transform = new AffineTransform();
    }

    public Shape getShape() {
        if( shapeDirty ) {
            if( image == null ) {
                return null;
            }
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = transform.createTransformedShape( rect );
            shapeDirty = false;
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape() != null && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getShape() == null ? null : getShape().getBounds();
    }

    public void paint( Graphics2D g ) {
        if( isVisible() && image != null ) {
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
        setBoundsDirty();
        repaint();
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
