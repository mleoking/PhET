/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.cck.util.ImageLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 17, 2003
 * Time: 6:23:35 AM
 * Copyright (c) Nov 17, 2003 by Sam Reid
 */
public class ImageIOImageLoader implements ImageLoader {
    private ClassLoader loader;

    public ImageIOImageLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public BufferedImage loadBufferedImage(String relativePath) throws IOException {
        return ImageIO.read(loader.getResource(relativePath));
    }
}
