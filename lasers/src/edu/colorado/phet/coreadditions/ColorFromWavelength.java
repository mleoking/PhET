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
        double redPct = (double)( color.getRed() ) / 255;
        double greenPct = (double)( color.getGreen() ) / 255;
        double bluePct = (double)( color.getBlue() ) / 255;
        double grayRefLevel = ( color.getRed() + color.getGreen() + color.getBlue() ) / ( 255 * 3 );
        for( int x = 0; x < src.getWidth(); x++ ) {
            for( int y = 0; y < src.getHeight(); y++ ) {
                int rgb = src.getRGB( x, y );
                int alpha = cm.getAlpha( rgb );
                double red = cm.getRed( rgb );
                double green = cm.getGreen( rgb );
                double blue = cm.getBlue( rgb );
                double gray = ( red + green + blue ) / ( 3 );
                int newRed = getComponent( gray, (double)color.getRed(), grayRefLevel );
                int newGreen = getComponent( gray, (double)color.getGreen(), grayRefLevel );
                int newBlue = getComponent( gray, (double)color.getBlue(), grayRefLevel );
                int newRGB = alpha * 0x01000000 + newRed * 0x00010000 + newGreen * 0x000000100 + newBlue * 0x00000001;
                dest.setRGB( x, y, newRGB );
            }
        }
        return dest;
    }

    public BufferedImage createCompatibleDestImage( BufferedImage src, ColorModel destCM ) {
        BufferedImage bi = new BufferedImage( src.getWidth(), src.getHeight(),
                                              src.getType(), (IndexColorModel)destCM );
        return bi;
    }

    /**
     * Does a piecewise linear interpolation to compute the component value
     *
     * @param grayLevel
     * @param componentRefLevel
     * @param grayRefLevel
     * @return
     */
    private int getComponent( double grayLevel, double componentRefLevel, double grayRefLevel ) {
        int result = 0;

        // if the grayLevel is 255, we simply return 255
        if( grayLevel == 255 ) {
            result = 255;
        }

        // if grayLevel is greater than grayRefLevel, do linear interpolation between (grayRefLevel,colorRefLevel)
        // and (255, 255 )
        if( grayLevel >= grayRefLevel && grayLevel < 255 ) {
            double m = ( 255 - componentRefLevel ) / ( 255 - grayRefLevel );
            double c = componentRefLevel + ( grayLevel - grayRefLevel ) * m;
            result = (int)c;
        }

        // if grayLevel is less than grayRefLevel, do linear interpolation between (grayRefLevel,colorRefLevel)
        // and (0, 0 )
        if( grayLevel <= grayRefLevel && grayLevel < 255 ) {
            double m = ( componentRefLevel ) / ( grayRefLevel );
            double c = ( grayLevel - grayRefLevel ) * m;
            result = (int)c;
        }
        return result;
    }
}

