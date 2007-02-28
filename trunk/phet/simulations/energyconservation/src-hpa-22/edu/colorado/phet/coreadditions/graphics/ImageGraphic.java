/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 3, 2003
 * Time: 9:40:28 PM
 * Copyright (c) Jun 3, 2003 by Sam Reid
 */
public class ImageGraphic implements Graphic {
    BufferedImage im;
    AffineTransform at;

    public ImageGraphic( BufferedImage im ) {
        this( im, new AffineTransform() );
    }

    public ImageGraphic( BufferedImage im, AffineTransform at ) {
        this.im = im;
        this.at = at;
    }

    public void setTransform( AffineTransform at ) {
        this.at = at;
    }

    public void setImage( BufferedImage im ) {
        this.im = im;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( im, at );
    }

    public Shape getShape() {
        Rectangle r = new Rectangle( 0, 0, im.getWidth(), im.getHeight() );
        Area a = new Area( r );
        a.transform( this.at );
        return a;
    }

    public BufferedImage getBufferedImage() {
        return im;
    }

}
