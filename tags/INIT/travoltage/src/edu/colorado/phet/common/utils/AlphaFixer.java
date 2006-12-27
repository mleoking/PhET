package edu.colorado.phet.common.utils;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

public class AlphaFixer {
    public static AlphaTable TABLE = new AlphaTable();

    public static BufferedImage patchAlpha( BufferedImage in ) {
        LookupOp lo = new LookupOp( TABLE, null );
        return lo.filter( in, null );
    }

    public static class AlphaTable extends LookupTable {
        public static final int[] ALPHA = new int[]{255, 0, 0, 0};

        public AlphaTable() {
            super( 0, 4 );
        }

        //turns pure red into something else.
        public int[] lookupPixel( int[] src, int[] dst ) {
            if( isWhite( src ) ) {
                //edu.colorado.phet.common.util.Debug.traceln("Returning alpha.");
                for( int i = 0; i < dst.length; i++ ) {
                    dst[i] = ALPHA[i];
                }
                return dst;
            }
//  	    //edu.colorado.phet.common.util.Debug.traceln("Returning source.");
            return src;
        }

        private boolean isWhite( int[] src ) {
            return ( src[1] == 255 && src[2] == 255 && src[3] == 255 );
        }
    }
}
