/**
 * Class: ColorFromWavelength
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Nov 10, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.lasers.coreadditions.VisibleColor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public class ColorFromWavelength implements BufferedImageOp {
    private double wavelength;

    public ColorFromWavelength( double wavelength ) {
        this.wavelength = wavelength;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

    public Rectangle2D getBounds2D( BufferedImage src ) {
        return new Rectangle2D.Double( 0, 0, src.getWidth(), src.getHeight() );
    }

    public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
        if( dstPt == null ) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation( srcPt.getX(), srcPt.getY() );
        return dstPt;
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dest ) {
        Color color = VisibleColor.wavelengthToColor( wavelength );
        if( dest == null ) {
            dest = createCompatibleDestImage( src, src.getColorModel() );
        }
        ColorModel cm = src.getColorModel();
        double redPct = (double)(color.getRed() ) / 255;
        double greenPct = (double)(color.getGreen() ) / 255;
        double bluePct = (double)(color.getBlue() ) / 255;
        for( int x = 0; x < src.getWidth(); x++ ) {
            for( int y = 0; y < src.getHeight(); y++ ) {
                int rgb = src.getRGB( x, y );
                if( rgb != 0 ) {
                    System.out.println( "&&&" );
                }
                int alpha = cm.getAlpha(  rgb );
                double red = cm.getRed( rgb );
                double green = cm.getGreen( rgb );
                double blue = cm.getBlue( rgb );
                double gray = ( red + green + blue ) / ( 3 );


            if( gray > 200 ) {
                System.out.println( "$$$" );

            }
//                double redPct = (double)red / 255;
//                double greenPct = (double)green / 255;
//                double bluePct = (double)blue / 255;
//                double gray = ( redPct + greenPct + bluePct ) / 3;

                double mr = ( color.getRed() - 255 ) / ( gray - 255 );
                double br = 255 * ( 1 - mr );
                double mg = ( color.getGreen() - 255 ) / ( gray - 255 );
                double bg = 255 * ( 1 - mg );
                double mb = ( color.getBlue() - 255 ) / ( gray - 255 );
                double bb = 255 * ( 1 - mb );

                int redNew = (int)( ( mr * gray) + br );
                int greenNew = (int)(( mg * gray) + bg );
                int blueNew = (int)(( mb * gray) + bb );

//                int redNew = (int)( ( 255 - color.getRed() ) * gray);
//                int blueNew = (int)(( 255 - color.getBlue() ) * gray);
//                int greenNew = (int)(( 255 - color.getGreen() ) * gray);
                int newRGB = alpha * 0x01000000 + redNew * 0x00010000 + greenNew * 0x00000100 + blueNew * 0x00000001;
                if( alpha != 0 ) {
                    dest.setRGB( x, y, newRGB );
//                    dest.setRGB( x, y, color.getRGB() );
                }
                //                dest.setRGB( x, y, rgb );
            }
        }
        return dest;
    }

    public BufferedImage createCompatibleDestImage( BufferedImage src, ColorModel destCM ) {
        BufferedImage bi = new BufferedImage( src.getWidth(), src.getHeight(),
                                              src.getType(), (IndexColorModel)destCM );
        return bi;
    }
}
