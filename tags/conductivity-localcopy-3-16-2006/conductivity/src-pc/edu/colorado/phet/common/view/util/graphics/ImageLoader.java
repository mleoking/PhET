/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util.graphics;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:34:20 AM
 * Copyright (c) Apr 16, 2003 by Sam Reid
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
        setImageIOLoader();
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
     * @return
     */
    public BufferedImage loadImage( String name ) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        URL imageUrl = cl.getResource( name );
        Image image = loadStrategy.loadImage( imageUrl );
        BufferedImage buffy = conversionStrategy.toBufferedImage( image );
        return buffy;
    }

    public void setImageIOLoader() {
        this.loadStrategy = new ImageIOLoader();
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

    static class ImageIOLoader implements LoadStrategy {
        public Image loadImage( URL location ) throws IOException {
            return ImageIO.read( location );
        }
    }

    static class PhetResourceLoader implements LoadStrategy {
        private static class ResourceLoader extends Container {
            public Image fetchImage( URL imageLocation ) throws IOException {
                Image image = null;
                try {
                    if( imageLocation == null ) {
                        System.out.println( "Image resource not found: " + imageLocation );
                        throw new IOException( "Image resource not found: " + imageLocation );
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