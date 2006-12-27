/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 4, 2003
 * Time: 10:44:04 PM
 * Copyright (c) Jun 4, 2003 by Sam Reid
 */
public class GraphicsUtilities {
    /**Returns the AffineTransform that will take the specified image, and place it centered at (x,y) at the specified angle.*/
    public static AffineTransform getImageTransform(BufferedImage bi, double angle, double x, double y) {
        AffineTransform at = new AffineTransform();
        at.translate(x - bi.getWidth() / 2, y - bi.getHeight() / 2);
        at.rotate(angle, bi.getWidth() / 2, bi.getHeight() / 2);
        return at;
    }

    public static AffineTransform getTransform(int width, int height, double angle, double x, double y) {
        AffineTransform at = new AffineTransform();
        at.translate(x - width / 2, y - height / 2);
        at.rotate(angle, width / 2, height / 2);
        return at;
    }
}
