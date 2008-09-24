/*, 2003.*/
// TODO: move up on level in package hierarchy
package edu.colorado.phet.semiconductor.phetcommon.view.util.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import edu.colorado.phet.semiconductor.phetcommon.view.util.GraphicsUtil;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:34:20 AM
 * <p/>
 * This level of granularity is required because different loaders return different
 * pixel data, and the final result is highly dependent on that
 * and the conversion mechanism.
 * <p/>
 * All combinations do not return the same results.
 */
public class ImageLoader {

    LoadStrategy loadStrategy;
    ConversionStrategy conversionStrategy;
    /**
     * A convenience instance, public for customization.
     */
    public static final ImageLoader instance = new ImageLoader();

    /**
     * Convenience method, uses the static instance to load a buffered image.
     */
    public static BufferedImage loadBufferedImage( String str ) throws IOException {
        return instance.loadImage( str );
    }

    public ImageLoader() {
//        setImageIOLoader();
        setPhetLoader();
        conversionStrategy = new ConversionStrategy() {
            public BufferedImage toBufferedImage( Image image ) {
                return GraphicsUtil.toBufferedImage( image );
            }
        };
    }

    /**
     * Loads an image using the current load strategy and converts it to a bufferedimage
     * using the current conversion strategy.
     *
     * @param name
     * @return the loaded image.
     */
    public BufferedImage loadImage( String name ) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        URL imageUrl = cl.getResource( name );
        if ( imageUrl == null ) {
            throw new IOException( "Null image URL for resource name=" + name );
        }
        Image image = loadStrategy.loadImage( imageUrl );
        BufferedImage buffy = conversionStrategy.toBufferedImage( image );
        return buffy;
    }

    public void setPhetLoader() {
        this.loadStrategy = new PhetResourceLoader();
    }

    static interface LoadStrategy {
        Image loadImage( URL location ) throws IOException;
    }

    static interface ConversionStrategy {
        BufferedImage toBufferedImage( Image image );
    }

    static class PhetResourceLoader implements LoadStrategy {
        private static class ResourceLoader extends Container {
            public Image fetchImage( URL imageLocation ) throws IOException {
                Image image = null;
                try {
                    if ( imageLocation == null ) {
                        throw new IOException( "Image resource not found: Null imagelocation URL" );
                    }
                    else {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        image = toolkit.createImage( imageLocation );
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

        public Image loadImage( URL location ) throws IOException {
            if ( location == null ) {
                throw new IOException( "Null URL Location" );
            }
            Image im = null;
            try {
                ResourceLoader r = new ResourceLoader();
                im = r.fetchImage( location );
            }
            catch( Throwable e ) {
                e.printStackTrace();
            }
            return im;

        }
    }
}