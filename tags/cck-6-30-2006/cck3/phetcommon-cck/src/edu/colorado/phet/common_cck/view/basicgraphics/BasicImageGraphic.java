/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:39 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class BasicImageGraphic extends BasicGraphic {
    private BufferedImage image;
    private AffineTransform transform;

    public BasicImageGraphic( BufferedImage image ) {
        this( image, new AffineTransform() );
    }

    public BasicImageGraphic( BufferedImage image, AffineTransform transform ) {
        this.image = image;
        this.transform = transform;
    }

    public Shape getShape() {
        Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
        return transform.createTransformedShape( rect );
    }

    public Rectangle getBounds() {
        return getShape().getBounds();
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, transform );
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
            boundsChanged();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Any side effects produced on this transform will not be automatically
     * observed by this class.
     */
    public AffineTransform getTransform() {
        return transform;
    }

}
