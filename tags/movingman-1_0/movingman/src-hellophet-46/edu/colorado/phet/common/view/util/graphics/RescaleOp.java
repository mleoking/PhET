/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 27, 2003
 * Time: 10:07:55 AM
 * Copyright (c) Apr 27, 2003 by Sam Reid
 */
public class RescaleOp {
    public static BufferedImage rescaleXMaintainAspectRatio( BufferedImage im, int x ) {
        double inx = im.getWidth();
        double dx = x / inx;
        return rescaleFractional( im, dx, dx );
    }

    public static BufferedImage rescale( BufferedImage in, int x, int y ) {
        double inx = in.getWidth();
        double iny = in.getHeight();
        double dx = x / inx;
        double dy = y / iny;
        return rescaleFractional( in, dx, dy );
    }

    public static BufferedImage rescaleFractional( BufferedImage in, double dx, double dy ) {
        AffineTransform at = AffineTransform.getScaleInstance( dx, dy );
        AffineTransformOp ato = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage out = ato.createCompatibleDestImage( in, in.getColorModel() );
        ato.filter( in, out );
        return out;
    }
}
