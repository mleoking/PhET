package phet.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageConverter {

    public static final BufferedImage toBufferedImageARGB( Image im, Component c )
            throws InterruptedException {
        MediaTracker mt = new MediaTracker( c );
        mt.addImage( im, 0 );
        mt.waitForAll();
        BufferedImage bi = new BufferedImage( im.getWidth( null ), im.getHeight( null ), BufferedImage.TYPE_INT_ARGB );//watch out for -1.

        Graphics2D g = (Graphics2D)bi.getGraphics();
//        g.setBackground( new Color( 0, 0, 0, 1.0f ) );
//        g.clearRect( 0, 0, im.getWidth( c ), im.getHeight( c ) );
        g.drawImage( im, new AffineTransform(), c );
        return bi;
    }
}
