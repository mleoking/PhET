/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.controllers.AbstractShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2003
 * Time: 1:34:25 AM
 * Copyright (c) Sep 17, 2003 by Sam Reid
 */
public class BufferedImageGraphic implements Graphic, AbstractShape {
    BufferedImage image;
    AffineTransform transform;
    Shape shape;

    public BufferedImageGraphic( BufferedImage image ) {
        this.image = image;
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
        this.shape = null;
    }

    public void paint( Graphics2D g ) {
        if( transform != null ) {
            g.drawRenderedImage( image, transform );
        }
    }

    public int getImageWidth() {
        return image.getWidth();
    }

    public int getImageHeight() {
        return image.getHeight();
    }

    public boolean containsPoint( Point pt ) {
        if( shape == null ) {
            this.shape = transform.createTransformedShape( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
        }
        return shape.contains( pt );
    }

    public void setImage( BufferedImage image ) {
        this.image = image;
        shape = null;//invalidate.
    }

}
