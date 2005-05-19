package phet.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Loads ARGB BufferedImages, without alpha patching.
 */
public class ResourceLoader3 implements ImageLoader {
    URLClassLoader loader;
    Component observer;

    public ResourceLoader3( URLClassLoader loader, Component observer ) {
        this.loader = loader;
        this.observer = observer;
    }

    public BufferedImage loadBufferedImage( String name ) {
        //URL fileLoc=findResource("images/"+name,c);
        URL fileLoc = loader.findResource( name );
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
