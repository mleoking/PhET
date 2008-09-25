/*  */
package edu.colorado.phet.conductivity.oldphetgraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 4:30:25 PM
 */

public class ImageGraphic implements Graphic {
    BufferedImage image;
    private AffineTransform transform = new AffineTransform();

    public ImageGraphic( BufferedImage image ) {
        this.image = image;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, transform );
    }

    public void setTransform( AffineTransform at ) {
        this.transform = at;
    }
}
