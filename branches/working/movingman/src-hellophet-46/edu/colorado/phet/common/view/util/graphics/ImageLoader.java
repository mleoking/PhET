/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util.graphics;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:34:20 AM
 * Copyright (c) Apr 16, 2003 by Sam Reid
 */
public class ImageLoader {
    public Image loadImage( String name ) {
        ResourceLoader r = new ResourceLoader();
        ResourceLoader.LoadedImageDescriptor lid = r.loadImage( name );
        Image im = lid.getImage();
        return im;
    }

    public BufferedImage loadBufferedImage( String name ) {
        return GraphicsUtil.toBufferedImage( loadImage( name ) );
    }

    private class ResourceLoader extends Container {

        public ResourceLoader.LoadedImageDescriptor loadImage( String imageLocation ) {
            Image image = null;
            ClassLoader cl = this.getClass().getClassLoader();
//        O.d("Cl="+cl);
            URL imageUrl = cl.getResource( imageLocation );
//        O.d("Str="+imageLocation);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
//        O.d("Toolkit="+toolkit);
//        O.d("ImageURL="+imageUrl);
            image = toolkit.createImage( imageUrl );
//        O.d("Image="+image);
            MediaTracker tracker = new MediaTracker( this );
            tracker.addImage( image, 0 );
            try {
                tracker.waitForAll();
            }
            catch( InterruptedException e ) {
            }
            return new ResourceLoader.LoadedImageDescriptor( image );
        }

//
// Inner classes
//

        /**
         * A class that describes an image that has been loaded by a
         * ResourceLoader
         */
        private class LoadedImageDescriptor {
            private Image image;
            private double width;
            private double height;

            LoadedImageDescriptor( Image image ) {
                this.image = image;
                this.width = image.getWidth( ResourceLoader.this );
                this.height = image.getHeight( ResourceLoader.this );
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
}
