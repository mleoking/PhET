package edu.colorado.phet.ohm1d.common.utils;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

public class AlphaFixer2 extends LookupTable {
    //public AlphaTable TABLE=new AlphaTable();
    public static final int[] ALPHA = new int[]{255, 0, 0, 0};
    int[] match;

    public AlphaFixer2( int[] match ) {
        super( 0, 4 );
        this.match = match;
    }

    public BufferedImage patchAlpha( BufferedImage in ) {
        LookupOp lo = new LookupOp( this, null );
        return lo.filter( in, null );
    }

    public int[] lookupPixel( int[] src, int[] dst ) {
        //util.Debug.traceln("Looking up source="+toString(src));
        //util.Debug.traceln("Match="+toString(match));
        if ( isWhite( src ) ) {
            //util.Debug.traceln("Returning alpha.");
            for ( int i = 0; i < dst.length; i++ ) {
                dst[i] = ALPHA[i];
            }
            return dst;
        }
        //util.Debug.traceln("Returning source.");
        return src;
    }

    private boolean isWhite( int[] src ) {
        return src[0] == match[0] && src[1] == match[1] && src[2] == match[2] && src[3] == match[3];
    }
}
