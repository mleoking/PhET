/*PhET, 2004.*/
package edu.colorado.phet.movingman.common;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 14, 2004
 * Time: 3:29:52 AM
 * Copyright (c) Jan 14, 2004 by Sam Reid
 */
public class ImageFlip3 {
    public static BufferedImage flipX( BufferedImage source ) {
        return flipXMacFriendly( source );
    }

    public static BufferedImage flipXMacFriendly( BufferedImage source ) {
        BufferedImage output = new BufferedImage( source.getWidth(), source.getHeight(), source.getType() );
        Graphics2D g2 = output.createGraphics();
        AffineTransform tx = createTransform( source );
        g2.drawRenderedImage( source, tx );
        return output;
    }

//    public static BufferedImage flipXOrig( BufferedImage source ) {
//        //        // Flip the image horizontally
//        AffineTransform tx = createTransform( source );
//        AffineTransformOp op;
//        op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
//        BufferedImage bufferedImage;
//        bufferedImage = op.filter( source, null );
//        return bufferedImage;
//    }

    private static AffineTransform createTransform( BufferedImage source ) {
        AffineTransform tx = AffineTransform.getScaleInstance( -1, 1 );
        tx.translate( -source.getWidth( null ), 0 );
        return tx;
    }
}
