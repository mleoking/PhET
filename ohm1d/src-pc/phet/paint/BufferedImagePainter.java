package phet.paint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BufferedImagePainter implements Painter {
    BufferedImage bi;
    AffineTransform at;

    public BufferedImagePainter( BufferedImage bi, int x, int y ) {
        this( bi, AffineTransform.getTranslateInstance( x, y ) );
    }

    public BufferedImagePainter( BufferedImage bi, AffineTransform at ) {
        this.bi = bi;
        this.at = at;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( bi, at );
    }
}


