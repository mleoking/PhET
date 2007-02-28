/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.util;

import java.awt.*;
import java.net.URL;

/**
 * Utility class for loading images
 */
public class ResourceLoaderCopy extends Container {

    public LoadedImageDescriptor loadImage( String imageLocation ) {
        if( imageLocation.indexOf( "\\" ) >= 0 ) {
            throw new RuntimeException( "JNLP cannot load from \\ delimited filenames." );
        }
        ClassLoader cl = this.getClass().getClassLoader();
        URL imageUrl = cl.getResource( imageLocation );
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage( imageUrl );
        MediaTracker tracker = new MediaTracker( this );
        tracker.addImage( image, 0 );
        try {
            tracker.waitForAll();
        }
        catch( InterruptedException e ) {
        }
        return new LoadedImageDescriptor( image );
    }

    /**
     * A class that describes an image that has been loaded by a
     * ResourceLoaderCopy
     */
    public class LoadedImageDescriptor {
        private Image image;
        private double width;
        private double height;

        LoadedImageDescriptor( Image image ) {
            this.image = image;
            this.width = image.getWidth( ResourceLoaderCopy.this );
            this.height = image.getHeight( ResourceLoaderCopy.this );
        }

        public Image getImage() {
            return image;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }
}
