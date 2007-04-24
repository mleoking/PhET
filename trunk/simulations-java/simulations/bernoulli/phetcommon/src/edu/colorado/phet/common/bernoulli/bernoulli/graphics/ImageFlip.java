/*, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jul 14, 2003
 * Time: 9:16:03 AM
 *
 */
public class ImageFlip {
    public static BufferedImage flipX(BufferedImage source) {
        //        // Flip the image horizontally
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-source.getWidth(null), 0);
        AffineTransformOp op;
        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage bufferedImage;
        bufferedImage = op.filter(source, null);
        return bufferedImage;
    }
}
