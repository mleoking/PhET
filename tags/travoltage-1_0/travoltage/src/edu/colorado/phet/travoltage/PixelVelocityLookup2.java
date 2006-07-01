package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.DoublePoint;

import java.util.Vector;

public class PixelVelocityLookup2 implements PixelVelocity {
    Pixel white = new Pixel( new int[]{255, 255, 255} );
    Pixel black = new Pixel( new int[]{0, 0, 0} );
    Vector pixels;
    Vector dp;

    public PixelVelocityLookup2() {
        pixels = new Vector();
        dp = new Vector();
    }

    /**
     * A Wrapper.
     */
    public static class Pixel {
        int[] a;

        public Pixel( int[] a ) {
            this.a = a;
        }

        public String toString() {
            Vector v = new Vector();
            for( int i = 0; i < a.length; i++ ) {
                v.add( new Integer( a[i] ) );
            }
            return v.toString();
        }

        public boolean equals( Object obj ) {
            if( obj instanceof Pixel ) {
                Pixel x = ( (Pixel)obj );
                for( int i = 0; i < a.length; i++ ) {
                    if( a[i] != x.a[i] ) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    public void add( int[] pixel, DoublePoint x ) {
        Pixel p = new Pixel( pixel );
        pixels.add( p );
        dp.add( x );
    }

    public DoublePoint getVelocity( int[] pixel ) {
        Pixel p = new Pixel( pixel );
        //edu.colorado.phet.common.util.Debug.traceln("Looking at pixel: "+p.toString());
        if( white.equals( p ) ) {
            return null;
        }
        if( black.equals( p ) ) {
            return null;
        }
        for( int i = 0; i < pixels.size(); i++ ) {
            Pixel x = (Pixel)pixels.get( i );
            if( x.equals( p ) ) {
                return ( (DoublePoint)dp.get( i ) );
            }
        }
        edu.colorado.phet.common.util.Debug.traceln( "No Velocity found for pixel: " + p.toString() );
        edu.colorado.phet.common.util.Debug.waitEnter();
        return null;
    }
}
