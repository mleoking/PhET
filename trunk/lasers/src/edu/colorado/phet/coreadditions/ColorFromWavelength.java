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
        for( int x = 0; x < src.getWidth(); x++ ) {
            for( int y = 0; y < src.getHeight(); y++ ) {
                int rgb = src.getRGB( x, y );
                int alpha = rgb & 0xFF000000;
                int red = rgb & 0x00FF0000;
                int green = rgb & 0x0000FF00;
                int blue = rgb & 0x000000FF;
                if( alpha != 0 ) {
                    dest.setRGB( x, y, color.getRGB() );
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
