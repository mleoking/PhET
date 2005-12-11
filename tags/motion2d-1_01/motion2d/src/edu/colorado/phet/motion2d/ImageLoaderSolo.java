/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.motion2d;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
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
public class ImageLoaderSolo {

    LoadStrategy loadStrategy;
    ConversionStrategy conversionStrategy;
    /**
     * A convenience INSTANCE, public for customization.
     */
    public static final ImageLoaderSolo INSTANCE = new ImageLoaderSolo();

    /**
     * Convenience method, uses the static INSTANCE to load a buffered image.
     */
    public static BufferedImage loadBufferedImage( String str ) throws IOException {
        return INSTANCE.loadImage( str );
    }

    public ImageLoaderSolo() {
        setPhetLoader();
        conversionStrategy = new ConversionStrategy() {
            public BufferedImage toBufferedImage( Image image ) {
                return convertToBufferedImage( image );
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
        if( imageUrl == null ) {
            throw new IOException( "Null image URL for resource name=" + name );
        }
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

    public void setToolkitLoader() {
        this.loadStrategy = new ToolkitLoader();
    }

    /**
     * So you can add your own strategy.
     */
    public void setLoadStrategy( LoadStrategy loadStrategy ) {
        this.loadStrategy = loadStrategy;
    }

    /**
     * So you can add your own strategy.
     */
    public void setConversionStrategy( ConversionStrategy conversionStrategy ) {
        this.conversionStrategy = conversionStrategy;
    }

    public void setIconLoader() {
        this.loadStrategy = new IconLoader();
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

    static class IconLoader implements LoadStrategy {
        public Image loadImage( URL location ) throws IOException {
            ImageIcon ic = new ImageIcon( location );
            return ic.getImage();
        }

    }

    static class ToolkitLoader implements LoadStrategy {
        public Image loadImage( URL location ) throws IOException {
            return Toolkit.getDefaultToolkit().getImage( location );
        }
    }

    static class PhetResourceLoader implements LoadStrategy {
        private static class ResourceLoader extends Container {
            public Image fetchImage( URL imageLocation ) throws IOException {
                Image image = null;
                try {
                    if( imageLocation == null ) {
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
            if( location == null ) {
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

    // This method returns a buffered image with the contents of an image
    // Taken from The Java Developer's Almanac, 1.4
    public static BufferedImage convertToBufferedImage( Image image ) {
        if( image instanceof BufferedImage ) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon( image ).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e665 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha( image );

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if( hasAlpha ) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage( image.getWidth( null ), image.getHeight( null ), transparency );
        }
        catch( HeadlessException e ) {
            // The system does not have a screen
        }

        if( bimage == null ) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if( hasAlpha ) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    // Taken from The Java Developer's Almanac, 1.4
    public static boolean hasAlpha( Image image ) {
        // If buffered image, the color model is readily available
        if( image instanceof BufferedImage ) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        try {
            pg.grabPixels();
        }
        catch( InterruptedException e ) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
}