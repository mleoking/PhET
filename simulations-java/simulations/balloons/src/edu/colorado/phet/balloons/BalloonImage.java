package edu.colorado.phet.balloons;

import java.awt.image.BufferedImage;

import edu.colorado.phet.balloons.common.paint.FixedImagePainter;

public class BalloonImage extends FixedImagePainter {
    BufferedImage empty;
    BufferedImage charged;

    public BalloonImage( int x, int y, BufferedImage empty, BufferedImage charged ) {
        super( x, y, empty );
        this.empty = empty;
        this.charged = charged;
    }

    public void setShowCharged( boolean t ) {
        if ( t ) {
            super.setImage( charged );
        }
        else {
            super.setImage( empty );
        }
    }
}
