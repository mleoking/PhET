/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.common;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jan 14, 2004
 * Time: 3:29:52 AM
 * Copyright (c) Jan 14, 2004 by Sam Reid
 */
public class ImageFlip3 {
    public static BufferedImage flipX(BufferedImage source) {
        //        // Flip the image horizontally
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-source.getWidth(null), 0);
        AffineTransformOp op;
        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage bufferedImage;
        bufferedImage = op.filter(source, null);
        return bufferedImage;
    }
}
