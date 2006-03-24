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
    public float[][] wavefunction;
    private float magnitude = 0.0f;
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
//                float v = valueAt( i, j );
//                a.setValue( i, j, v*( 0.5 ) );
//                b.setValue( i, j, v*( 0.5 ) );
//            }
//        }
//    }
//
//    public void combineWaves( Rectangle region, Lattice2D a, Lattice2D b ) {
//        for( int i = region.x; i < region.x + region.width; i++ ) {
//            for( int j = region.y; j < region.y + region.height; j++ ) {
//                float sum = SplitModel.sumMagnitudes( a.valueAt( i, j ), b.valueAt( i, j ) );
//                setValue( i, j, new float( sum, 0 ) );
//            }
//        }
//    }

    public void maximize() {
        float max = 0;
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                max = Math.max( Math.abs( getValue( i, j ) ), max );
            }
        }
        scale( 1.0f / max );
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

    public double getAverageValue( int x, int y, int windowWidth ) {
        double sum = 0;
        int count = 0;
        for( int i = x - windowWidth; i <= x + windowWidth; i++ ) {
            for( int j = y - windowWidth; j <= y + windowWidth; j++ ) {
                if( containsLocation( i, j ) ) {
                    sum += getValue( i, j );
                    count++;
                }
            }
        }
        return sum / count;
    }

    public static interface Listener {
        void cleared();

        void scaled( float scale );
    }

    public Lattice2D( int width, int height ) {
        wavefunction = new float[width][height];
        clear();
    }

    public Lattice2D( float[][] values ) {
        this.wavefunction = values;
        setMagnitudeDirty();
    }

    public void setMagnitude( float newMagnitude ) {
        setMagnitudeDirty();
        float origMagnitude = getMagnitude();
        scale( (float)( Math.sqrt( newMagnitude ) / Math.sqrt( origMagnitude ) ) );
        float m = getMagnitude();//todo remove this after we're sure its working
        if( Math.abs( m - newMagnitude ) > 10E-6 ) {
            throw new RuntimeException( "Normalization failed: requested new magnitude=" + newMagnitude + ", received=" + m );
        }
    }

    public void scale( float scale ) {
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

//    public static String toString( float[] complexes ) {
//        return Arrays.asList( complexes ).toString();
//    }

    public static float[][] newInstance( int w, int h ) {
        float[][] copy = new float[w][h];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = 0;
            }
        }
        return copy;
    }

    public static float[][] copy( float[][] w ) {
        float[][] copy = new float[w.length][w[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = w[i][j];
            }
        }
        return copy;
    }

    public void copyTo( Lattice2D dest ) {
        dest.setSize( getWidth(), getHeight() );
        int width = getWidth();
        int height = getHeight();//for speed
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                dest.wavefunction[i][j] = wavefunction[i][j];
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

    public void setValue( int i, int j, float value ) {
        wavefunction[i][j] = value;
    }

    public float getValue( int i, int j ) {
        return wavefunction[i][j];
    }

    public void normalize() {
        float totalProbability = getMagnitude();
//        System.out.println( "totalProbability = " + totalProbability );
        float scale = (float)( 1.0 / Math.sqrt( totalProbability ) );
        scale( scale );
        float postProb = getMagnitude();
//        System.out.println( "postProb = " + postProb );

        float diff = (float)( 1.0 - postProb );
        float err = Math.abs( diff );
        if( err > 0.0001 ) {
            System.out.println( "Error in probability normalization, norm=" + postProb + ", err=" + err );
//            new Exception("Error in probability normalization, norm=" + postProb  ).printStackTrace( );
//            throw new RuntimeException( "Error in probability normalization." );
        }
        setMagnitudeDirty();
    }

    public float getMagnitude() {
        if( magnitudeDirty ) {
//            System.out.println( "Wavefunction.getMagnitude" );
            this.magnitude = recomputeMagnitude();
            magnitudeDirty = false;
        }
        return magnitude;
    }

    private float recomputeMagnitude() {//        System.out.println( "Get Magnitude @ t="+System.currentTimeMillis() );
        float runningSum = 0.0f;
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                float psiStar = wavefunction[i][j];
                float psi = wavefunction[i][j];
                float term = psiStar * psi;
                runningSum = runningSum + term;
            }
        }
        return runningSum;
    }

    public void clear() {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
//                if( valueAt( i, j ) == null ) {
//                    wavefunction[i][j] = new float();
//                }
//                else {
//                    valueAt( i, j ).zero();
//                }
                setValue( i, j, 0 );
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
            wavefunction = new float[width][height];
            clear();
        }

    }

    public Lattice2D copy() {
        float[][] copy = new float[getWidth()][getHeight()];
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                copy[i][j] = getValue( i, j );
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
                sub.wavefunction[i - x][j - y] = ( getValue( i, j ) );
            }
        }
        return sub;
    }

    public void add( Lattice2D w ) {
        if( w.getWidth() == getWidth() && w.getHeight() == getHeight() ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int k = 0; k < getHeight(); k++ ) {
                    wavefunction[i][k] += w.getValue( i, k );
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
                float val = getValue( i, k );
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
