/** Sam Reid*/
package net.jmge.gif;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:09:16 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class TestJMGE {

    // ...
    public static void writeAnimatedGIF( Image[] still_images,
                                         String annotation,
                                         boolean looped,
                                         double frames_per_second,
                                         OutputStream out ) throws IOException {
        Gif89Encoder gifenc = new Gif89Encoder();
        for( int i = 0; i < still_images.length; ++i ) {
            gifenc.addFrame( still_images[i] );
        }
        gifenc.setComments( annotation );
        gifenc.setLoopCount( looped ? 0 : 1 );
        gifenc.setUniformDelay( (int)Math.round( 100 / frames_per_second ) );
        gifenc.encode( out );
    }

}
