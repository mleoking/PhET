/** Sam Reid*/
package to.mumble;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 11:55:32 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class AnimGIFTest {
    public static void saveImage( BufferedImage[] im ) throws IOException {
        OutputStream os = new FileOutputStream( "out.gif" );
        AnimGifEncoder age = new AnimGifEncoder( os );
        for( int i = 0; i < im.length; i++ ) {
            BufferedImage image = im[i];
            age.add( image );
        }
        age.encode();
    }
}
