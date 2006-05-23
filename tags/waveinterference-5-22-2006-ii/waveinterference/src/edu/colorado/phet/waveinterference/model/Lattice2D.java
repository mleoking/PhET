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
    float[][] wavefunction;

    public Lattice2D( Lattice2D lattice2D ) {
        this( lattice2D.wavefunction );
    }

    public Lattice2D( int width, int height ) {
        wavefunction = new float[width][height];
        clear();
    }

    public Lattice2D( float[][] values ) {
        this.wavefunction = values;
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

    public Dimension getSize() {
        return new Dimension( getWidth(), getHeight() );
    }

    public static interface Listener {
        void cleared();

        void scaled( float scale );
    }

    public void scale( float scale ) {
        if( scale != 1.0 ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int j = 0; j < getHeight(); j++ ) {
                    wavefunction[i][j] *= scale;
                }
            }
        }
    }

    public boolean containsLocation( int i, int k ) {
        return getBounds().contains( i, k );
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

    public void clear() {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                setValue( i, j, 0 );
            }
        }
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
