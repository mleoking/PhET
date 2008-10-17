/*, 2003.*/
package edu.colorado.phet.greenhouse.phetcommon.view.util.graphics;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:34:20 AM
 *
 */
public class ImageLoader {

//    private static Logger s_logger = Logger.getLogger( "edu.colorado.phet.PhetLogger" );

    public Image loadImage( String name ) {
        Image im = null;
        try {
            ResourceLoader r = new ResourceLoader();
            im = r.fetchImage( name );
        }
        catch( Throwable e ) {
            e.printStackTrace();
        }
        return im;
    }

    public BufferedImage loadBufferedImage( String name ) {
        return BufferedImageUtils.toBufferedImage( loadImage( name ) );
    }

    public static Image fetchImage( String name ) {
        Image im = null;
        try {
            ResourceLoader r = new ResourceLoader();
            im = r.fetchImage( name );
        }
        catch( Throwable e ) {
            e.printStackTrace();
        }
        return im;
    }

    public static BufferedImage fetchBufferedImage( String name ) {
        return BufferedImageUtils.toBufferedImage( fetchImage( name ) );
    }


    private static class ResourceLoader extends Container {

        public Image fetchImage( String imageLocation ) {

            Image image = null;
            try {
                ClassLoader cl = this.getClass().getClassLoader();
                URL imageUrl = cl.getResource( imageLocation );
                if( imageUrl == null ) {
                    System.out.println( "Image resource not found: " + imageLocation );
//                    s_logger.severe( "Image resource not found: " + imageLocation );
                }
                else {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    image = toolkit.createImage( imageUrl );
                    MediaTracker tracker = new MediaTracker( this );
                    tracker.addImage( image, 0 );
                    tracker.waitForAll();
                }
            }
            catch( InterruptedException e ) {
            }
            return image;
        }
    }
}
