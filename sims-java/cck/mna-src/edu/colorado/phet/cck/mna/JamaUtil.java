/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

import Jama.Matrix;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 10:07:09 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class JamaUtil {
    public static boolean equals( Matrix a, Matrix b ) {
        if( a.getRowDimension() == b.getRowDimension() && a.getColumnDimension() == b.getColumnDimension() ) {
            for( int i = 0; i < a.getRowDimension(); i++ ) {
                for( int j = 0; j < a.getColumnDimension(); j++ ) {
                    if( a.get( i, j ) != b.get( i, j ) ) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static Matrix deleteRow( Matrix m, int row ) {
        Matrix dst = new Matrix( m.getRowDimension() - 1, m.getColumnDimension() );
        for( int i = 0; i < dst.getRowDimension(); i++ ) {
            for( int j = 0; j < dst.getColumnDimension(); j++ ) {
                int srcRow = ( i < row ) ? i : i + 1;
                dst.set( i, j, m.get( srcRow, j ) );
            }
        }
        return dst;
    }

    public static Matrix deleteColumn( Matrix m, int col ) {
        Matrix dst = new Matrix( m.getRowDimension(), m.getColumnDimension() - 1 );
        for( int i = 0; i < dst.getRowDimension(); i++ ) {
            for( int j = 0; j < dst.getColumnDimension(); j++ ) {
                int srcCol = ( j < col ) ? j : j + 1;
                dst.set( i, j, m.get( i, srcCol ) );
            }
        }
        return dst;
    }
//    public static Matrix deleteColumn( Matrix m, int col ) {
//
//    }
}
