package edu.colorado.phet.util;

import edu.colorado.phet.util.ImageConverter;
import edu.colorado.phet.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Loads ARGB BufferedImages, without alpha patching.
 */
public class ResourceLoader3 implements ImageLoader {
    ClassLoader loader;
    Component observer;

    public ResourceLoader3( ClassLoader loader, Component observer ) {
        this.loader = loader;
        this.observer = observer;
    }

    public BufferedImage loadBufferedImage( String name ) {
        //URL fileLoc=findResource("images/"+name,c);
        URL fileLoc = loader.getResource( name );
        try {
            Image img = observer.getToolkit().createImage( fileLoc );
            BufferedImage argb = ImageConverter.toBufferedImageARGB( img, observer );
            return argb;
        }
        catch( Exception e ) {
            throw new RuntimeException( e.toString() + " -> looked for resource : " + name + ", fileLoc=" + fileLoc );
        }
    }
}
