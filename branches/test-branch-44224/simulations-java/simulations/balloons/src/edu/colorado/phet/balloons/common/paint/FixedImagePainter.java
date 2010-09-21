package edu.colorado.phet.balloons.common.paint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FixedImagePainter implements Painter {
    AffineTransform at;
    int x;
    int y;
    BufferedImage im;

    public FixedImagePainter( BufferedImage im ) {
        this( 0, 0, im );
    }

    public FixedImagePainter( int x, int y, BufferedImage im ) {
        this.x = x;
        this.y = y;
        at = AffineTransform.getTranslateInstance( x, y );
        this.im = im;
    }

    public Point getPosition() {
        return new Point( x, y );
    }

    public void setImage( BufferedImage im ) {
        this.im = im;
    }

    public void setPosition( Point p ) {
        this.x = p.x;
        this.y = p.y;
        at = AffineTransform.getTranslateInstance( x, y );
    }

    public BufferedImage getImage() {
        return im;
    }

    public void paint( Graphics2D g ) {
        if ( im != null ) {
            g.drawRenderedImage( im, at );
        }
    }
}
