/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.util.Animation;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:27:37 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DUtil {
    public static void main( String[] args ) throws IOException {
        Animation animation = new Animation( "animations/pusher/pusher-3", 19 );
        System.out.println( "animation = " + animation );
        for( int i = 0; i < animation.getNumFrames(); i++ ) {
            BufferedImage image = animation.getFrame( i );
            
        }
    }
}
