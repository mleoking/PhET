/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Wavefunction;
import org.opensourcephysics.numerics.FFT2D;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 11:53:56 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */

public class QWIFFT2D {

    /*
    *  * The data is stored in a 1-dimensional array in Row-Major order.
* The physical layout in the array data, of the mathematical data d[i,j] is as follows:
* <PRE>
*    Re(d[i,j]) = data[i*rowspan + 2*j]
*    Im(d[i,j]) = data[i*rowspan + 2*j + 1]
* </PRE>
*     where <code>rowspan</code> must be at least 2*ncols (it defaults to 2*ncols).
    */
    public static double[] toArray( Wavefunction phi ) {
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

    public static Wavefunction forwardFFT( Wavefunction psi ) {
        FFT2D fft2D = new FFT2D( psi.getWidth(), psi.getHeight() );
        double[]data = toArray( psi );
        fft2D.transform( data );
        return parseData( data, psi.getWidth(), psi.getHeight() );
    }

    public static Wavefunction parseData( double[] data, int numRows, int numCols ) {
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

    public static Wavefunction inverseFFT( Wavefunction phi ) {
        FFT2D fft2D = new FFT2D( phi.getWidth(), phi.getHeight() );
        double[]data = QWIFFT2D.toArray( phi );
        fft2D.backtransform( data );
        return parseData( data, phi.getWidth(), phi.getHeight() );
    }
}
