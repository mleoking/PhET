/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 2, 2004
 * Time: 12:35:20 AM
 * Copyright (c) Jan 2, 2004 by Sam Reid
 */
public class SimpleBufferedImageGraphic implements Graphic, Boundary {
    BufferedImage image;
    AffineTransform transform;

    public SimpleBufferedImageGraphic( BufferedImage image ) {
        this.image = image;
        if( image == null ) {
            throw new RuntimeException( "Null image." );
        }
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
    }

    public SimpleBufferedImageGraphic( BufferedImage image, AffineTransform transform ) {
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
    }

    public void setPosition( Point ctr ) {
        setTransform( getCenterTransform( ctr ) );
    }

    private AffineTransform getCenterTransform( Point ctr ) {
        double imWidth = image.getWidth();
        double imHeight = image.getHeight();
        AffineTransform imageTransform = AffineTransform.getTranslateInstance( ctr.x - imWidth / 2, ctr.y - imHeight / 2 );
        return imageTransform;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void setPosition( int centerX, int centerY ) {
        setPosition( new Point( centerX, centerY ) );
    }

    public boolean contains( int x, int y ) {
        return getShape().contains( x, y );
    }

    public Shape getShape() {
        Rectangle r = new Rectangle( image.getWidth(), image.getHeight() );
        Shape sh = transform.createTransformedShape( r );
        return sh;
    }
}
