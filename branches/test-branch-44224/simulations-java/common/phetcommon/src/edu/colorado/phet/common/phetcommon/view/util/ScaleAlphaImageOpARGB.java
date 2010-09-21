/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

/**
 * ScaleAlphaImageOpARGB scales the alpha channel of a BufferedImage.
 * The BufferedImage must be of TYPE_INT_ARGB.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScaleAlphaImageOpARGB implements BufferedImageOp {

    private double _scale;

    public ScaleAlphaImageOpARGB() {
        this( 1 );
    }

    public ScaleAlphaImageOpARGB( double scale ) {
        super();
        _scale = scale;
    }

    public void setScale( double scale ) {
        _scale = scale;
    }

    public double getScale() {
        return _scale;
    }

    public BufferedImage createCompatibleDestImage( BufferedImage src, ColorModel destCM ) {
        AffineTransformOp op = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        return op.createCompatibleDestImage( src, destCM );
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dest ) {

        if ( src.getType() != BufferedImage.TYPE_INT_ARGB && src.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
            throw new UnsupportedOperationException( "unsupported BufferedImage type=" + src.getType() );
        }

        WritableRaster srcRaster = src.getRaster();
        int[] srcBuffer = ( (DataBufferInt) srcRaster.getDataBuffer() ).getData();

        if ( dest == null ) {
            dest = createCompatibleDestImage( src, src.getColorModel() );
        }
        WritableRaster destRaster = dest.getRaster();
        int[] destBuffer = ( (DataBufferInt) destRaster.getDataBuffer() ).getData();

        for ( int i = 0; i < srcBuffer.length; i++ ) {
            int argb = srcBuffer[i];
            int alpha = ( argb >> 24 ) & 0x000000FF;
            int scaledAlpha = (int) ( _scale * alpha );
            int scaledPixel = ( scaledAlpha << 24 ) | ( argb & 0x00FFFFFF );
            destBuffer[i] = scaledPixel;
        }

        return dest;
    }

    public Rectangle2D getBounds2D( BufferedImage src ) {
        return new Rectangle2D.Double( 0, 0, src.getWidth(), src.getHeight() );
    }

    public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
        if ( dstPt == null ) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation( srcPt.getX(), srcPt.getY() );
        return dstPt;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

}
