/**
 * User: University of Colorado, PhET
 * Date: Jun 25, 2004
 * Time: 5:59:39 PM
 * Latest Change:
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private boolean shapeDirty = true;
    private Shape shape;

    protected PhetImageGraphic( Component component ) {
        this( component, null, 0, 0 );
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
        this( component, image, 0, 0 );
    }

    public PhetImageGraphic( Component component, BufferedImage image, int x, int y ) {
        super( component );
        this.image = image;
        setLocation( x, y );
    }

    public Shape getShape() {
        AffineTransform transform = getTransform();
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
            g.drawRenderedImage( image, getTransform() );
        }
    }

    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }

    public void setBoundsDirty() {
        super.setBoundsDirty();
        shapeDirty = true;
    }

    public void setImage( BufferedImage image ) {
        if( this.image != image ) {
            this.image = image;
            setBoundsDirty();
            autorepaint();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

}
