/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:34:20 AM
 * Copyright (c) Apr 16, 2003 by Sam Reid
 */
public class CommonImageLoader implements ImageLoader {
    public BufferedImage loadBufferedImage(String name) {
        ResourceLoaderCopy r = new ResourceLoaderCopy();
//        O.d("RLC="+r);
        ResourceLoaderCopy.LoadedImageDescriptor lid = r.loadImage(name);
//        O.d("Lid="+lid);
        Image im = lid.getImage();
//        O.d("Image="+im);
        try {
            return ImageConverter.toBufferedImageARGB(im, r);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            throw new RuntimeException(e);
        }
    }

}
