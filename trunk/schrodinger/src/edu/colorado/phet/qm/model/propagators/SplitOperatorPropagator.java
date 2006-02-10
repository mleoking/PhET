/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;
import org.opensourcephysics.numerics.FFT2D;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:35:37 PM
 * Copyright (c) Jun 28, 2005 by Sam Reid
 */

public class SplitOperatorPropagator extends Propagator {
    double dt = 0.01;
    private double mass = 1.0;

    public SplitOperatorPropagator( Potential potential ) {
        super( potential );
    }

    public void propagate( Wavefunction w ) {
        Wavefunction psi = new Wavefunction( w );
        Wavefunction expV = ones( psi.getWidth(), psi.getHeight() );
        Wavefunction expT = getExpT( w.getWidth(), w.getHeight() );
        psi = multiplyPointwise( expV, psi );
        Wavefunction phi = forwardFFT( psi );
        phi = multiplyPointwise( expT, phi );
        psi = inverseFFT( phi );
        w.setWavefunction( w );
    }

    private Wavefunction getExpT( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, getExpTValue( i, k ) );
            }
        }
        return wavefunction;
    }

    private Complex getExpTValue( int i, int j ) {
        Vector2D.Double p = new Vector2D.Double( i, j );
        double pSquared = p.getMagnitudeSq();
        double numerator = pSquared * dt / 2 / mass;
//        Complex value = new Complex( 0, -1 );
//        value.scale( numerator );
        return Complex.exponentiateImaginary( -numerator );
    }

    private Wavefunction ones( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, 1, 0 );
            }
        }
        return wavefunction;
    }

    private Wavefunction inverseFFT( Wavefunction phi ) {
        FFT2D fft2D = new FFT2D( phi.getWidth(), phi.getHeight() );
        double[]data = toArray( phi );
        fft2D.transform( data );
        return parseData( data, phi.getWidth(), phi.getHeight() );
    }

    private Wavefunction parseData( double[] data, int numRows, int numCols ) {
        Wavefunction wavefunction = new Wavefunction( numRows, numCols );
        int rowspan = 2 * numCols;
        for( int i = 0; i < numRows; i++ ) {
            for( int j = 0; j < numCols; j++ ) {
                double real = data[i * rowspan + 2 * j];
                double im = data[i * rowspan + 2 * j + 1];
                wavefunction.setValue( i, j, real, im );
            }
        }
        return wavefunction;
    }


    /*
    *  * The data is stored in a 1-dimensional array in Row-Major order.
* The physical layout in the array data, of the mathematical data d[i,j] is as follows:
* <PRE>
*    Re(d[i,j]) = data[i*rowspan + 2*j]
*    Im(d[i,j]) = data[i*rowspan + 2*j + 1]
* </PRE>
*     where <code>rowspan</code> must be at least 2*ncols (it defaults to 2*ncols).
    */
    private double[] toArray( Wavefunction phi ) {
        double[]data = new double[phi.getWidth() * phi.getHeight() * 2];
        int rowspan = 2 * phi.getWidth();
        for( int i = 0; i < phi.getWidth(); i++ ) {
            for( int j = 0; j < phi.getHeight(); j++ ) {
                data[i * rowspan + 2 * j] = phi.valueAt( i, j ).getReal();
                data[i * rowspan + 2 * j + 1] = phi.valueAt( i, j ).getImaginary();
            }
        }
        return data;
    }

    private Wavefunction forwardFFT( Wavefunction psi ) {
        return psi;
    }

    private Wavefunction multiplyPointwise( Wavefunction a, Wavefunction b ) {
        Wavefunction result = new Wavefunction( a.getWidth(), b.getWidth() );
        for( int i = 0; i < result.getWidth(); i++ ) {
            for( int k = 0; k < result.getHeight(); k++ ) {
                result.setValue( i, k, a.valueAt( i, k ).times( b.valueAt( i, k ) ) );
            }
        }
        return a;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new SplitOperatorPropagator( getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }

    public static void main( String[] args ) {
        
    }
}
