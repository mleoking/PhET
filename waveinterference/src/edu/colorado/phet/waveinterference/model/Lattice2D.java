/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.text.DecimalFormat;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 1:46:56 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Lattice2D {
    public double[][] wavefunction;
    private double magnitude = 0.0;
    private boolean magnitudeDirty = true;

    public Lattice2D( Lattice2D lattice2D ) {
        this( lattice2D.wavefunction );
    }

    public void clearRect( Rectangle rect ) {
        for( int i = rect.x; i < rect.x + rect.width; i++ ) {
            for( int j = rect.y; j < rect.y + rect.height; j++ ) {
                //todo intersect rectangles to avoid contains call each time.
                if( containsLocation( i, j ) ) {
                    setValue( i, j, 0 );
                }
            }
        }
    }
//
//    public void splitWave( Rectangle region, Lattice2D a, Lattice2D b ) {
//        for( int i = region.x; i < region.x + region.width; i++ ) {
//            for( int j = region.y; j < region.y + region.height; j++ ) {
//                double v = valueAt( i, j );
//                a.setValue( i, j, v*( 0.5 ) );
//                b.setValue( i, j, v*( 0.5 ) );
//            }
//        }
//    }
//
//    public void combineWaves( Rectangle region, Lattice2D a, Lattice2D b ) {
//        for( int i = region.x; i < region.x + region.width; i++ ) {
//            for( int j = region.y; j < region.y + region.height; j++ ) {
//                double sum = SplitModel.sumMagnitudes( a.valueAt( i, j ), b.valueAt( i, j ) );
//                setValue( i, j, new double( sum, 0 ) );
//            }
//        }
//    }

    public void maximize() {
        double max = 0;
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                max = Math.max( Math.abs( valueAt( i, j ) ), max );
            }
        }
        scale( 1.0 / max );
        setMagnitudeDirty();
    }

    public Lattice2D getNormalizedInstance() {
        Lattice2D w = copy();
        w.normalize();
        return w;
    }

    public Dimension getSize() {
        return new Dimension( getWidth(), getHeight() );
    }

    public static interface Listener {
        void cleared();

        void scaled( double scale );
    }

    public Lattice2D( int width, int height ) {
        wavefunction = new double[width][height];
        clear();
    }

    public Lattice2D( double[][] values ) {
        this.wavefunction = values;
        setMagnitudeDirty();
    }

    public void setMagnitude( double newMagnitude ) {
        setMagnitudeDirty();
        double origMagnitude = getMagnitude();
        scale( Math.sqrt( newMagnitude ) / Math.sqrt( origMagnitude ) );
        double m = getMagnitude();//todo remove this after we're sure its working
        if( Math.abs( m - newMagnitude ) > 10E-6 ) {
            throw new RuntimeException( "Normalization failed: requested new magnitude=" + newMagnitude + ", received=" + m );
        }
    }

    public void scale( double scale ) {
        if( scale != 1.0 ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int j = 0; j < getHeight(); j++ ) {
                    wavefunction[i][j] *= scale;
                }
            }
        }
        setMagnitudeDirty();
    }

    public boolean containsLocation( int i, int k ) {
        return getBounds().contains( i, k );
    }

//    public static String toString( double[] complexes ) {
//        return Arrays.asList( complexes ).toString();
//    }

    public static double[][] newInstance( int w, int h ) {
        double[][] copy = new double[w][h];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = 0;
            }
        }
        return copy;
    }

    public static double[][] copy( double[][] w ) {
        double[][] copy = new double[w.length][w[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = w[i][j];
            }
        }
        return copy;
    }

    public void copyTo( Lattice2D dest ) {
        dest.setSize( getWidth(), getHeight() );
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                dest.setValue( i, j, valueAt( i, j ) );
//                dest.setValue( i, j, new double( valueAt( i, j ) ) );
            }
        }
        dest.setMagnitudeDirty();
    }

    public int getWidth() {
        return wavefunction.length;
    }

    public int getHeight() {
        return wavefunction[0].length;
    }

    public void setValue( int i, int j, double value ) {
        wavefunction[i][j] = value;
    }

    public double valueAt( int i, int j ) {
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
        double err = Math.abs( diff );
        if( err > 0.0001 ) {
            System.out.println( "Error in probability normalization, norm=" + postProb + ", err=" + err );
//            new Exception("Error in probability normalization, norm=" + postProb  ).printStackTrace( );
//            throw new RuntimeException( "Error in probability normalization." );
        }
        setMagnitudeDirty();
    }

    public double getMagnitude() {
        if( magnitudeDirty ) {
//            System.out.println( "Wavefunction.getMagnitude" );
            this.magnitude = recomputeMagnitude();
            magnitudeDirty = false;
        }
        return magnitude;
    }

    private double recomputeMagnitude() {//        System.out.println( "Get Magnitude @ t="+System.currentTimeMillis() );
        double runningSum = 0.0;
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                double psiStar = wavefunction[i][j];
                double psi = wavefunction[i][j];
                double term = psiStar * psi;
                runningSum = runningSum + term;
            }
        }
        return runningSum;
    }

    public void clear() {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
//                if( valueAt( i, j ) == null ) {
//                    wavefunction[i][j] = new double();
//                }
//                else {
//                    valueAt( i, j ).zero();
//                }
                setValue( 0, 0, 0 );
            }
        }
        setMagnitudeDirty();
//        notifyCleared();
    }

    public void setMagnitudeDirty() {
        magnitudeDirty = true;
    }

    public void setSize( int width, int height ) {
        if( getWidth() != width || getHeight() != height ) {
            wavefunction = new double[width][height];
            clear();
        }

    }

    public Lattice2D copy() {
        double[][] copy = new double[getWidth()][getHeight()];
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                copy[i][j] = valueAt( i, j );
            }
        }
        return new Lattice2D( copy );
    }

    public Rectangle getBounds() {
        return new Rectangle( getWidth(), getHeight() );
    }

    public Lattice2D copyRegion( int x, int y, int width, int height ) {
        Lattice2D sub = new Lattice2D( width, height );
        for( int i = x; i < x + width; i++ ) {
            for( int j = y; j < y + height; j++ ) {
                sub.wavefunction[i - x][j - y] = ( valueAt( i, j ) );
            }
        }
        return sub;
    }

    public void add( Lattice2D w ) {
        if( w.getWidth() == getWidth() && w.getHeight() == getHeight() ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int k = 0; k < getHeight(); k++ ) {
                    wavefunction[i][k] += w.valueAt( i, k );
                }
            }
        }
        else {
            throw new RuntimeException( "illegal arg dim" );
        }
        setMagnitudeDirty();
    }

    public void setWavefunction( Lattice2D lattice2D ) {
        clear();
        add( lattice2D );
    }

    public Lattice2D createEmptyWavefunction() {
        return new Lattice2D( getWidth(), getHeight() );
    }

    public void printWaveToScreen() {
        printWaveToScreen( new DecimalFormat( "0.00" ) );
    }

    public void printWaveToScreen( DecimalFormat formatter ) {
        for( int k = 0; k < getHeight(); k++ ) {
            for( int i = 0; i < getWidth(); i++ ) {
                double val = valueAt( i, k );
                String s = formatter.format( val );
                if( s.equals( formatter.format( -0.000000001 ) ) ) {
                    s = formatter.format( 0 );
                }
                String spaces = "  ";
                if( s.startsWith( "-" ) ) {
                    spaces = " ";
                }
                System.out.print( spaces + s );

            }
            System.out.println( "" );
        }
    }
}
