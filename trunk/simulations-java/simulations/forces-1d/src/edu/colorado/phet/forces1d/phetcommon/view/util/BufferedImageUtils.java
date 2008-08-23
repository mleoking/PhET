/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.view.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

import javax.swing.*;

/**
 * BufferedImageUtils
 *
 * @author ?
 * @version $Revision$
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
        int width = (int) ( in.getWidth() * dx );
        int height = (int) ( in.getHeight() * dy );
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
        int type = source.getType();
        if ( source.getType() == 0 ) {
            type = BufferedImage.TYPE_INT_ARGB;  //This is a hack that works.
        }
        BufferedImage output = new BufferedImage( source.getWidth(), source.getHeight(), type );
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

    /**
     * Creates and returns a buffered image that is a rotated version of a specified
     * buffered image. The transform is done so that the image is not truncated, and
     * occupies the minimum bounding box
     *
     * @param bImage
     * @param theta  Angle the image is to be rotated, in radians.
     * @return the rotated image.
     */
    public static BufferedImage getRotatedImage( BufferedImage bImage, double theta ) {
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
        double x = 0;
        double y = 0;
        double alpha = 0;
        double w = bImage.getWidth();
        double h = bImage.getHeight();
        if ( theta >= 0 && theta <= Math.PI / 2 ) {
            x = h * Math.sin( theta );
            y = 0;
        }
        if ( theta > Math.PI / 2 && theta <= Math.PI ) {
            alpha = theta - Math.PI / 2;
            x = w * Math.sin( alpha ) + h * Math.cos( alpha );
            y = h * Math.sin( alpha );
        }
        if ( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            alpha = theta - Math.PI;
            x = w * Math.cos( alpha );
            y = w * Math.sin( alpha ) + h * Math.cos( alpha );
        }
        // Works
        if ( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            alpha = Math.PI * 2 - theta;
            x = 0;
            y = w * Math.sin( alpha );
        }
        AffineTransform atx = AffineTransform.getTranslateInstance( x, y );
        atx.rotate( theta );
        BufferedImageOp op = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage result = op.filter( bImage, null );
        return result;
    }

    // This method returns true if the specified image has transparent pixels
    // Taken from The Java Developer's Almanac, 1.4
    public static boolean hasAlpha( Image image ) {
        // If buffered image, the color model is readily available
        if ( image instanceof BufferedImage ) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        try {
            pg.grabPixels();
        }
        catch( InterruptedException e ) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    /**
     * Gets the transparency of an image.
     *
     * @param image the image
     * @return OPAQUE, BITMASK or TRANSLUCENT (see java.awt.Transparency)
     */
    public static int getTransparency( Image image ) {
        // If buffered image, the color model is readily available
        if ( image instanceof BufferedImage ) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().getTransparency();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        try {
            pg.grabPixels();
        }
        catch( InterruptedException e ) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();

        int transparency = Transparency.OPAQUE;
        if ( cm != null ) {
            transparency = cm.getTransparency();
        }
        return transparency;
    }

    // This method returns a buffered image with the contents of an image
    // Taken from The Java Developer's Almanac, 1.4
    public static BufferedImage toBufferedImage( Image image ) {
        if ( image instanceof BufferedImage ) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon( image ).getImage();

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Create the buffered image
            int transparency = getTransparency( image );
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage( image.getWidth( null ), image.getHeight( null ), transparency );
        }
        catch( HeadlessException e ) {
            // The system does not have a screen
        }

        if ( bimage == null ) {
            // Create a buffered image using the default color model
            boolean hasAlpha = hasAlpha( image );
            int type = BufferedImage.TYPE_INT_RGB;
            if ( hasAlpha ) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bimage;
    }
}