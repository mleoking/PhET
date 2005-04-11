/*
 * Class: ResourceLoader
 * Package: edu.colorado.phet.graphicaldomain.util
 *
 * Created by: Ron LeMaster
 * Date: Oct 29, 2002
 */
package edu.colorado.phet.graphics.util;

import java.awt.*;
import java.net.URL;

/**
 *  Utility class for loading images
 */
public class ResourceLoader extends Container {

    public LoadedImageDescriptor loadImage( String imageLocation ) {
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

    //
    // Inner classes
    //

    /**
     * A class that describes an image that has been loaded by a
     * ResourceLoader
     */
    public class LoadedImageDescriptor {
        private Image image;
        private float  width;
        private float  height;

        LoadedImageDescriptor( Image image ) {
            this.image = image;
            this.width = image.getWidth( ResourceLoader.this );
            this.height = image.getHeight( ResourceLoader.this );
        }

        public Image getImage() {
            return image;
        }

        public float  getWidth() {
            return width;
        }

        public float  getHeight() {
            return height;
        }
    }
}
