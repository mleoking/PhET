/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:39 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PrimaryImageGraphic extends PrimaryGraphic {
    private BufferedImage image;
    private AffineTransform transform;
    private boolean shapeDirty = true;
    private Shape shape;

    public PrimaryImageGraphic( Component component, BufferedImage image, AffineTransform transform ) {
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
//
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

    public void setState( int x, int y, double scale ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x, y );
        tx.scale( scale, scale );
        if( getTransform().equals( tx ) ) {
            return;
        }
        setTransform( tx );
    }

    public void setState( int x, int y ) {
        AffineTransform tx = AffineTransform.getTranslateInstance( x, y );
        if( getTransform().equals( tx ) ) {
            return;
        }
        setTransform( tx );
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
        setBoundsDirty();
        shapeDirty = true;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public AffineTransform getTransform() {
        return transform;
    }
}
