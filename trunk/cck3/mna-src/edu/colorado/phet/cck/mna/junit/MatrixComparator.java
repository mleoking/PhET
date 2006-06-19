/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit;

import Jama.Matrix;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 10:07:09 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class MatrixComparator {
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
}
