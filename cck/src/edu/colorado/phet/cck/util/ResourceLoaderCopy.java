/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.util;

import java.awt.*;
import java.net.URL;

/**
 * Utility class for loading images
 */
public class ResourceLoaderCopy extends Container {

    public LoadedImageDescriptor loadImage(String imageLocation) {
        if (imageLocation.indexOf("\\") >= 0)
            throw new RuntimeException("JNLP cannot load from \\ delimited filenames.");
        ClassLoader cl = this.getClass().getClassLoader();
//        O.d("Cl="+cl);
        URL imageUrl = cl.getResource(imageLocation);
//        O.d("Str="+imageLocation);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        O.d("Toolkit="+toolkit);
//        O.d("ImageURL="+imageUrl);
        Image image = toolkit.createImage(imageUrl);
//        O.d("Image="+image);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
        }
        return new LoadedImageDescriptor(image);
    }

    //
    // Inner classes
    //

    /**
     * A class that describes an image that has been loaded by a
     * ResourceLoaderCopy
     */
    public class LoadedImageDescriptor {
        private Image image;
        private double width;
        private double height;

        LoadedImageDescriptor(Image image) {
            this.image = image;
            this.width = image.getWidth(ResourceLoaderCopy.this);
            this.height = image.getHeight(ResourceLoaderCopy.this);
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
