/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.view.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 27, 2003
 * Time: 10:07:55 AM
 * Copyright (c) Apr 27, 2003 by Sam Reid
 */
public class BufferedImageUtils {
//    public static BufferedImage rescaleYMaintainAspectRatio( BufferedImage im, int height ) {
//        double iny = im.getHeight();
//        double dy = height / iny;
//        return rescaleFractional( im, dy, dy );
//    }
//
//    public static BufferedImage rescaleXMaintainAspectRatio( BufferedImage im, int width ) {
//        double inx = im.getWidth();
//        double dx = width / inx;
//        return rescaleFractional( im, dx, dx );
//    }
//
//    public static BufferedImage rescale( BufferedImage in, int x, int y ) {
//        double inx = in.getWidth();
//        double iny = in.getHeight();
//        double dx = x / inx;
//        double dy = y / iny;
//        return rescaleFractional( in, dx, dy );
//    }
//
//    public static BufferedImage rescaleFractional( BufferedImage in, double dx, double dy ) {
//        AffineTransform at = AffineTransform.getScaleInstance( dx, dy );
//        AffineTransformOp ato = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
//        BufferedImage out = ato.createCompatibleDestImage( in, in.getColorModel() );
//        ato.filter( in, out );
//        return out;
//    }

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
//            newImage=SwingUtils.toBufferedImage( created );
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

    public static BufferedImage flipY( BufferedImage source ) {
        return flipYMacFriendly( source );
    }

    public static BufferedImage flipYMacFriendly( BufferedImage source ) {
        BufferedImage output = new BufferedImage( source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = output.createGraphics();
        AffineTransform tx = createTransformFlipY( source );
        g2.drawRenderedImage( source, tx );
        return output;
    }

    public static BufferedImage flipX( BufferedImage source ) {
        return flipXMacFriendly( source );
    }

    public static BufferedImage flipXMacFriendly( BufferedImage source ) {
        BufferedImage output = new BufferedImage( source.getWidth(), source.getHeight(), source.getType() );
        Graphics2D g2 = output.createGraphics();
        AffineTransform tx = createTransformFlipX( source );
        g2.drawRenderedImage( source, tx );
        return output;
    }

//    public static BufferedImage flipXOrig( BufferedImage source ) {
//        //        // Flip the image horizontally
//        AffineTransform tx = createTransformFlipX( source );
//        AffineTransformOp op;
//        op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
//        BufferedImage bufferedImage;
//        bufferedImage = op.filter( source, null );
//        return bufferedImage;
//    }

    private static AffineTransform createTransformFlipY( BufferedImage source ) {
        AffineTransform tx = AffineTransform.getScaleInstance( 1, -1 );
        tx.translate( 0, -source.getHeight() );
        return tx;
    }

    private static AffineTransform createTransformFlipX( BufferedImage source ) {
        AffineTransform tx = AffineTransform.getScaleInstance( -1, 1 );
        tx.translate( -source.getWidth(), 0 );
        return tx;
    }
}

