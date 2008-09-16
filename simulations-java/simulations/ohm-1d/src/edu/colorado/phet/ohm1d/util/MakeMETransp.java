package edu.colorado.phet.ohm1d.util;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

public class MakeMETransp extends LookupTable {

    public MakeMETransp() {
        super( 0, 4 );
    }

    public BufferedImage patchAlpha( BufferedImage in ) {
        LookupOp lo = new LookupOp( this, null );
        return lo.filter( in, null );
    }

    public int[] lookupPixel( int[] src, int[] dst ) {
        //util.Debug.traceln("Looking up source="+toString(src));
        //util.Debug.traceln("Match="+toString(match));

        //util.Debug.traceln("Returning alpha.");
        for ( int i = 0; i < dst.length; i++ ) {
            dst[i] = src[i];
        }
        if ( dst.length == 4 ) {
            dst[3] = (int) ( src[3] * .7 );
        }
        return dst;
        //util.Debug.traceln("Returning source.");
    }

}
