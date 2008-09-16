package edu.colorado.phet.ohm1d.common.utils;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

public class MakeTransparentImage extends LookupTable {
    int alpha;

    public MakeTransparentImage( int alpha ) {
        super( 0, 4 );
        this.alpha = alpha;
    }

    public BufferedImage patchAlpha( BufferedImage in ) {
        LookupOp lo = new LookupOp( this, null );
        return lo.filter( in, null );
    }

    public int[] lookupPixel( int[] src, int[] dst ) {
        //util.Debug.traceln("Returning alpha.");
        for ( int i = 0; i < dst.length; i++ ) {
            dst[i] = src[i];
        }
        dst[dst.length - 1] = alpha;
        return dst;
    }
}
