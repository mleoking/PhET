/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch.bulb;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

/**
 * User: Sam Reid
 * Date: May 20, 2003
 * Time: 11:49:35 PM
 * Copyright (c) May 20, 2003 by Sam Reid
 */
public class BulbBrightness {

    private BulbTransform lt;
    private LookupOp lo;
    private BufferedImage out = null;

    public BulbBrightness() {
        RenderingHints rh = new RenderingHints( null );
        rh.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        this.lt = new BulbTransform();
        this.lo = new LookupOp( lt, rh );
    }

    public BufferedImage operate( BufferedImage root, double intensity ) {
        System.out.println( "intensity = " + intensity );
        lt.brightness = intensity;
        if( out == null ) {
            out = lo.createCompatibleDestImage( root, root.getColorModel() );
        }

//        System.out.println("root.getColorModel() = " + root.getColorModel());
        return lo.filter( root, out );
    }

    public static class BulbTransform extends LookupTable {
        double brightness = 1;

        public BulbTransform() {
            super( 0, 4 );
        }

        public int[] lookupPixel( int[] src, int[] dest ) {
            if( isGray( src ) ) {
                copy( src, dest );
                return dest;
            }
            else {
                for( int i = 0; i < src.length - 1; i++ ) {
                    int pixel = (int)( ( src[i] - 255 ) * brightness + 255 );
                    dest[i] = pixel;
                }
                src[3] = (int)( 255 * brightness );
                return dest;
            }
        }

        private void copy( int[] src, int[] dest ) {
            System.arraycopy( src, 0, dest, 0, src.length );
        }

        private boolean isGray( int[] src ) {
            int val = src[0];
            int maxDist = 0;
            for( int i = 0; i < src.length - 1; i++ ) {
                int dist = Math.abs( src[i] - val );
                if( dist > maxDist ) {
                    maxDist = dist;
                }
            }
//            if (maxDist > 140)
            if( maxDist > 112 ) {
                return false;
            }
            return true;
        }

    }

}
