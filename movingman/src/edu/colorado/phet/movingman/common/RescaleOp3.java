/*PhET, 2004.*/
package edu.colorado.phet.movingman.common;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 14, 2004
 * Time: 3:18:29 AM
 * Copyright (c) Jan 14, 2004 by Sam Reid
 */
public class RescaleOp3 {
    public static BufferedImage rescaleYMaintainAspectRatio( Component parent, BufferedImage im, int height ) {
        double iny = im.getHeight();
        double dy = height / iny;
        return rescaleFractional( parent, im, dy, dy );
    }

//    public static BufferedImage rescaleXMaintainAspectRatio( BufferedImage im, int width ) {
//        double inx = im.getWidth();
//        double dx = width / inx;
//        return rescaleFractional( im, dx, dx );
//    }

//    public static BufferedImage rescale( BufferedImage in, int x, int y ) {
//        double inx = in.getWidth();
//        double iny = in.getHeight();
//        double dx = x / inx;
//        double dy = y / iny;
//        return rescaleFractional( in, dx, dy );
//    }

    public static BufferedImage rescaleFractional( Component parent, BufferedImage in, double dx, double dy ) {
        //could test for MAC, or try/catch, or just pretend everybody is a mac.
        return rescaleFractionalMacFriendly( parent, in, dx, dy );
//        AffineTransform at = AffineTransform.getScaleInstance( dx, dy );
//        AffineTransformOp ato = new AffineTransformOp( at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
//        BufferedImage out = ato.createCompatibleDestImage( in, in.getColorModel() );
//        ato.filter( in, out );
//        return out;
    }

    public static BufferedImage rescaleFractionalMacFriendly( Component parent, BufferedImage in, double dx, double dy ) {
        int width = (int)( in.getWidth() * dx );
        int height = (int)( in.getHeight() * dy );
        BufferedImage newImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
//        Image created = parent.createImage( width, height );
//        BufferedImage newImage = null;
//        if( created instanceof BufferedImage ) {
//            newImage = (BufferedImage)created;
//        }
//        else{
//            newImage=GraphicsUtil.toBufferedImage( created );
//            System.out.println( "Component returned non-BufferedImage type." );
//        }
        Graphics2D g2 = newImage.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance( dx, dy );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2.drawRenderedImage( in, at );
        return newImage;

    }
}