/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 1:46:56 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Wavefunction {

    private Complex[][] wavefunction;

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void cleared();

        void scaled( double scale );
    }

    private void notifyCleared() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.cleared();
        }
    }

    public Wavefunction( int width, int height ) {
        wavefunction = new Complex[width][height];
        clear();
    }

    public Wavefunction( Complex[][] values ) {
        this.wavefunction = values;
    }

    public void setMagnitude( double newScale ) {
        double totalProbability = getMagnitude();
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( scale * Math.sqrt( newScale ) );

        double m = getMagnitude();//todo remove this after we're sure its working
        if( Math.abs( m - newScale ) > 10E-6 ) {
            throw new RuntimeException( "Normalization failed: requested=" + newScale + ", received=" + m );
        }
    }

    public void scale( double scale ) {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                Complex complex = wavefunction[i][j];
                complex.scale( scale );
            }
        }
    }

    public boolean containsLocation( int i, int k ) {
        return getBounds().contains( i, k );
    }

//    public static boolean containsLocation( Complex[][] wavefunction, int i, int k ) {
//        return i >= 0 && i < wavefunction.length && k >= 0 && k < wavefunction[0].length;
//    }

    public static String toString( Complex[] complexes ) {
        return Arrays.asList( complexes ).toString();
    }

    public static Complex[][] newInstance( int w, int h ) {
        Complex[][] copy = new Complex[w][h];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex();
            }
        }
        return copy;
    }

    public static Complex[][] copy( Complex[][] w ) {
        Complex[][] copy = new Complex[w.length][w[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex( w[i][j] );
            }
        }
        return copy;
    }

    public void copyTo( Wavefunction copy ) {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                copy.valueAt( i, j ).setValue( valueAt( i, j ) );
            }
        }
    }

//    public static void copy( Complex[][] src, Complex[][] dst ) {
//        for( int i = 0; i < src.length; i++ ) {
//            for( int j = 0; j < src[i].length; j++ ) {
//                dst[i][j].setValue( src[i][j] );
//            }
//        }
//    }

    public int getWidth() {
        return wavefunction.length;
    }

    public int getHeight() {
        return wavefunction[0].length;
    }

    public void setValue( int i, int j, Complex complex ) {
        wavefunction[i][j] = complex;
    }

    public Complex valueAt( int i, int j ) {
        return wavefunction[i][j];
    }

    public void normalize() {
        double totalProbability = getMagnitude();
//        System.out.println( "totalProbability = " + totalProbability );
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( scale );
        double postProb = getMagnitude();
//        System.out.println( "postProb = " + postProb );

        double diff = 1.0 - postProb;
        if( !( Math.abs( diff ) < 0.0001 ) ) {
            System.out.println( "Error in probability normalization." );
//            throw new RuntimeException( "Error in probability normalization." );
        }
    }

    public double getMagnitude() {
        Complex runningSum = new Complex();
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                Complex psiStar = wavefunction[i][j].complexConjugate();
                Complex psi = wavefunction[i][j];
                Complex term = psiStar.times( psi );
                runningSum = runningSum.plus( term );
            }
        }
        return runningSum.abs();
    }

    public void clear() {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                if( valueAt( i, j ) == null ) {
                    setValue( i, j, new Complex() );
                }
                else {
                    valueAt( i, j ).zero();
                }
            }
        }
        notifyCleared();
    }

    public void setSize( int width, int height ) {
        wavefunction = new Complex[width][height];
    }

    public Wavefunction copy() {
        Complex[][] copy = new Complex[getWidth()][getHeight()];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[0].length; j++ ) {
                copy[i][j] = valueAt( i, j ).copy();
            }
        }
        return new Wavefunction( copy );
    }

    public Rectangle getBounds() {
        return new Rectangle( getWidth(), getHeight() );
    }

    public Wavefunction copyRegion( int x, int y, int width, int height ) {
        Wavefunction sub = new Wavefunction( width, height );
        for( int i = x; i < x + width; i++ ) {
            for( int j = y; j < y + height; j++ ) {
                sub.wavefunction[i - x][j - y] = new Complex( valueAt( i, j ) );
            }
        }
        return sub;
    }

    public void add( Wavefunction w ) {
        if( w.getWidth() == getWidth() && w.getHeight() == getHeight() ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int k = 0; k < getHeight(); k++ ) {
                    valueAt( i, k ).add( w.valueAt( i, k ) );
                }
            }
        }
        else {
            throw new RuntimeException( "illegal arg dim" );
        }
    }

    public void setWavefunction( Wavefunction wavefunction ) {
        clear();
        add( wavefunction );
    }

    public Wavefunction createEmptyWavefunction() {
        return new Wavefunction( getWidth(), getHeight() );
    }
}
