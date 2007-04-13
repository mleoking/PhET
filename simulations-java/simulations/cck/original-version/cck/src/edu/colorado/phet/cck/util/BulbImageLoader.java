/** Sam Reid*/
package edu.colorado.phet.cck.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 18, 2004
 * Time: 11:44:37 PM
 * Copyright (c) May 18, 2004 by Sam Reid
 */
public class BulbImageLoader {
    public static BufferedImage loadImage( String name ) {
        ResourceLoaderCopy r = new ResourceLoaderCopy();
//        O.d("RLC="+r);
        ResourceLoaderCopy.LoadedImageDescriptor lid = r.loadImage( name );
//        O.d("Lid="+lid);
        Image im = lid.getImage();
//        O.d("Image="+im);
        try {
            return toBufferedImageARGB( im, r );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            throw new RuntimeException( e );
        }
    }

    public static final BufferedImage toBufferedImageRGB( Image im, Component c )
            throws InterruptedException {
        //helper.ThreadHelper.nap(1000);
        MediaTracker mt = new MediaTracker( c );
        mt.addImage( im, 0 );
        mt.waitForAll();
        BufferedImage bi = new BufferedImage( im.getWidth( null ), im.getHeight( null ), BufferedImage.TYPE_INT_RGB );//watch out for -1.
        Graphics2D g = (Graphics2D)bi.getGraphics();
        g.drawImage( im, new AffineTransform(), null );
        return bi;
    }

    public static final BufferedImage toBufferedImageARGB( Image im, Component c )
            throws InterruptedException {
        //helper.ThreadHelper.nap(1000);
        MediaTracker mt = new MediaTracker( c );
        mt.addImage( im, 0 );
        mt.waitForAll();
        BufferedImage bi = new BufferedImage( im.getWidth( null ), im.getHeight( null ), BufferedImage.TYPE_INT_ARGB );//watch out for -1.
        Graphics2D g = (Graphics2D)bi.getGraphics();
        g.drawImage( im, new AffineTransform(), null );
        return bi;
    }
}
