// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 9:53:39 AM
 */

public class ImageFade {
    //    private double scaleFactor = 0.95;
    private int dVal = -10;

    public ImageFade() {
        this( -1 );
    }

    public ImageFade( int dVal ) {
        this.dVal = dVal;
    }

    public void fade( BufferedImage image ) {
        WritableRaster raster = image.getRaster();
        float[] pixel = raster.getPixel( 0, 0, (float[])null );
        for( int i = 0; i < image.getWidth(); i++ ) {
            for( int j = 0; j < image.getHeight(); j++ ) {
                raster.getPixel( i, j, pixel );
                for( int k = 0; k < pixel.length; k++ ) {
//                    float distFromUnity = 255 - pixel[k];
//                    distFromUnity *= scaleFactor;
//                    pixel[k] = (float)MathUtil.clamp( 0, 255 - distFromUnity, 255 );
                    pixel[k] += dVal;
                    if( pixel[k] > 255 ) {
                        pixel[k] = 255;
                    }
                    if( pixel[k] < 0 ) {
                        pixel[k] = 0;
                    }
                }
                raster.setPixel( i, j, pixel );
            }
        }
    }
}
