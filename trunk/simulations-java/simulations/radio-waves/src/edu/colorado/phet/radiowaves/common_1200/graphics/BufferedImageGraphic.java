package edu.colorado.phet.radiowaves.common_1200.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 2, 2004
 * Time: 12:35:20 AM
 */
public class BufferedImageGraphic implements BoundedGraphic {
    private BufferedImage image;
    protected AffineTransform transform = new AffineTransform();
    private boolean shapeDirty = true;
    private Shape sh;

    public BufferedImageGraphic( BufferedImage image ) {
        this.image = image;
        if( image == null ) {
            throw new RuntimeException( "Null image." );
        }
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
        shapeDirty = true;
    }

    public BufferedImageGraphic( BufferedImage image, AffineTransform transform ) {
        this.image = image;
        this.transform = transform;
    }

    public void paint( Graphics2D graphics2D ) {
        if( image != null && transform != null ) {
            graphics2D.drawRenderedImage( image, transform );
        }
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
        this.shapeDirty = true;
    }

    public void setLocation( Point ctr ) {
        setLocation( ctr.x, ctr.y );
    }

    public void setLocation( int centerX, int centerY ) {
        setTransform( getCenterTransform( centerX, centerY ) );
    }

    public AffineTransform getCenterTransform( int x, int y ) {
        double imWidth = image.getWidth();
        double imHeight = image.getHeight();
        AffineTransform imageTransform = AffineTransform.getTranslateInstance( x - imWidth / 2, y - imHeight / 2 );
        return imageTransform;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public boolean contains( int x, int y ) {
        return getShape().contains( x, y );
    }

    public Shape getShape() {
        if( shapeDirty || sh == null ) {
            Rectangle r = new Rectangle( image.getWidth(), image.getHeight() );
            sh = transform.createTransformedShape( r );
            shapeDirty = false;
        }
        return sh;
    }
}