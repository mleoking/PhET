// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.jamaphet;

import Jama.Matrix;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Useful functions and utilities for use with Jama
 */
public class JamaUtils {

    /*---------------------------------------------------------------------------*
    * conversion from phetcommon vectors to matrices
    *----------------------------------------------------------------------------*/

    public static Matrix rowVector( final Vector2D vector ) {
        return new Matrix( 2, 1 ) {{
            set( 0, 0, vector.getX() );
            set( 0, 1, vector.getY() );
        }};
    }

    public static Matrix rowVector( final ImmutableVector3D vector ) {
        return new Matrix( 3, 1 ) {{
            set( 0, 0, vector.getX() );
            set( 0, 1, vector.getY() );
            set( 0, 2, vector.getZ() );
        }};
    }

    public static Matrix columnVector( final Vector2D vector ) {
        return new Matrix( 2, 1 ) {{
            set( 0, 0, vector.getX() );
            set( 1, 0, vector.getY() );
        }};
    }

    public static Matrix columnVector( final ImmutableVector3D vector ) {
        return new Matrix( 3, 1 ) {{
            set( 0, 0, vector.getX() );
            set( 1, 0, vector.getY() );
            set( 2, 0, vector.getZ() );
        }};
    }

    /**
     * Create a Matrix where each column is a 2D vector
     */
    public static Matrix matrixFromVectors2D( final List<Vector2D> vectors ) {
        final int n = vectors.size();
        return new Matrix( 2, n ) {{
            for ( int i = 0; i < n; i++ ) {
                // fill the vector into the matrix as a column
                Vector2D v = vectors.get( i );
                set( 0, i, v.getX() );
                set( 1, i, v.getY() );
            }
        }};
    }

    /**
     * Create a Matrix where each column is a 3D vector
     */
    public static Matrix matrixFromVectors3D( final List<ImmutableVector3D> vectors ) {
        final int n = vectors.size();
        return new Matrix( 3, n ) {{
            for ( int i = 0; i < n; i++ ) {
                // fill the vector into the matrix as a column
                ImmutableVector3D v = vectors.get( i );
                set( 0, i, v.getX() );
                set( 1, i, v.getY() );
                set( 2, i, v.getZ() );
            }
        }};
    }

    /*---------------------------------------------------------------------------*
    * conversions from matrices to phetcommon vectors
    *----------------------------------------------------------------------------*/

    public static Vector2D vectorFromMatrix2D( Matrix matrix, int column ) {
        return new Vector2D( matrix.get( 0, column ), matrix.get( 1, column ) );
    }

    public static ImmutableVector3D vectorFromMatrix3D( Matrix matrix, int column ) {
        return new ImmutableVector3D( matrix.get( 0, column ), matrix.get( 1, column ), matrix.get( 2, column ) );
    }

    /*---------------------------------------------------------------------------*
    * miscellaneous
    *----------------------------------------------------------------------------*/

    public static String matrixString( Matrix m ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "dim: " + m.getRowDimension() + "x" + m.getColumnDimension() + "\n" );
        for ( int row = 0; row < m.getRowDimension(); row++ ) {
            for ( int col = 0; col < m.getColumnDimension(); col++ ) {
                builder.append( m.get( row, col ) + " " );
            }
            builder.append( "\n" );
        }
        return builder.toString();
    }
}
