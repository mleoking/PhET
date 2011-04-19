// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

/**
 * Algorithm that is used to compute pixels hit in the white light algorithm, necessary since we have to
 * implement additive color mixing ourselves (because java only supports subtractive mixing internally).
 */
public class BresenhamLineAlgorithm {
    //See http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
    public void draw( int x0, int y0, int x1, int y1 ) {
        int dx = Math.abs( x1 - x0 );
        int dy = Math.abs( y1 - y0 );
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while ( true ) {
            setPixel( x0, y0 );
            if ( x0 == x1 && y0 == y1 ) { break; }
            if ( isOutOfBounds( x0, y0 ) ) {break;}
            int e2 = 2 * err;
            if ( e2 > -dy ) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if ( e2 < dx ) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
    }

    //Shortcut method to avoid computing points outside of the relevant bounds
    public boolean isOutOfBounds( int x0, int y0 ) {
        return false;
    }

    //Sets the specified pixel to be hit (this implementation just prints debug info, but should be overriden to do something useful)
    public void setPixel( int x0, int y0 ) {
        System.out.println( x0 + ", " + y0 );
    }

    public static void main( String[] args ) {
        new BresenhamLineAlgorithm().draw( 0, 0, 10, 5 );
    }
}
