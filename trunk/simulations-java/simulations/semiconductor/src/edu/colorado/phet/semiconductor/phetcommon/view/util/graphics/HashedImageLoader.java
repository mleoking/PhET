package edu.colorado.phet.semiconductor.phetcommon.view.util.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Decorates ImageLoader with buffering.
 */
public class HashedImageLoader extends ImageLoader {
    Hashtable buffer = new Hashtable();

    public BufferedImage loadImage( String image ) throws IOException {
        if( buffer.containsKey( image ) ) {
            return (BufferedImage)buffer.get( image );
        }
        else {
            BufferedImage imageLoad = super.loadImage( image );
            buffer.put( image, imageLoad );
            return imageLoad;
        }
    }
}
