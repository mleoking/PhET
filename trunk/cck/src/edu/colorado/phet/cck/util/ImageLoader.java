package edu.colorado.phet.cck.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 9:33:40 AM
 * Copyright (c) Apr 16, 2003 by Sam Reid
 */
public interface ImageLoader {

    public BufferedImage loadBufferedImage(String name) throws IOException;

}
