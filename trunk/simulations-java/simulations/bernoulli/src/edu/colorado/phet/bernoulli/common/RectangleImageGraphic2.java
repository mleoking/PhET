package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 23, 2003
 * Time: 2:29:58 AM
 */
public class RectangleImageGraphic2 implements Graphic {
    BufferedImage image;
    Rectangle outputRect;
    AffineTransform at;
    private boolean visible = true;

    public RectangleImageGraphic2( BufferedImage image ) {
        this.image = image;
    }

    public void paint( Graphics2D g ) {
        if( at != null && visible ) {
            g.drawRenderedImage( image, at );
        }
    }

    public void setOutputRect( Rectangle outputRect ) {
        this.outputRect = outputRect;
        at = new AffineTransform();
        double sx = outputRect.getWidth() / image.getWidth();
        double sy = outputRect.getHeight() / image.getHeight();
        at.scale( sx, sy );
        at.translate( outputRect.x / sx, outputRect.y / sy );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}