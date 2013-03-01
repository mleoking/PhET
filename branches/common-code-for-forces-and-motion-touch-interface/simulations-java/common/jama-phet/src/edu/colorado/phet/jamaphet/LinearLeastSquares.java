package edu.colorado.phet.jamaphet;

import Jama.Matrix;

/**
 * Linear least-squares regression using Jama for matrices, with related functions that use it for best-fit.
 *
 * @author Jonathan Olson
 */
public class LinearLeastSquares {

    // minimizes linear least-squares for an over-constrained matrix equation Ax=b (finds x)
    public static Matrix solveLinearLeastSquares( Matrix a, Matrix b ) {
        // uses x* = (A' A)^-1 A' b
        Matrix aTranspose = a.transpose();
        Matrix toInvert = aTranspose.times( a );
        Matrix inverted = toInvert.inverse(); // A' A is a square matrix

        return inverted.times( aTranspose ).times( b );
    }
}
