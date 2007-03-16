/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.cck.util.CommonImageLoader;
import edu.colorado.phet.cck.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 19, 2003
 * Time: 10:46:27 AM
 * Copyright (c) Nov 19, 2003 by Sam Reid
 */
public class CommonImageLoader2 implements ImageLoader {
    CommonImageLoader cil = new CommonImageLoader();

    public CommonImageLoader2() {
    }

    public BufferedImage loadBufferedImage( String name ) throws IOException {
        return cil.loadBufferedImage( name );
    }
}
