/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna.junit;

import Jama.Matrix;
import edu.colorado.phet.cck.mna.JamaUtil;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 10:22:59 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class TestJamaUtil extends TestCase {
    public void testDeleteRow() {
        Matrix a = new Matrix( new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}} );
        Matrix b = new Matrix( new double[][]{
                {1, 2, 3},
                {7, 8, 9}
        } );
        assertEquals( true, JamaUtil.equals( b, JamaUtil.deleteRow( a, 1 ) ) );
        assertEquals( false, JamaUtil.equals( b, JamaUtil.deleteRow( a, 0 ) ) );
        assertEquals( false, JamaUtil.equals( b, JamaUtil.deleteRow( a, 2 ) ) );
    }

    public void testDeleteRowCol() {
        Matrix a = new Matrix( new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}} );
        Matrix b = new Matrix( new double[][]{
                {1, 3},
                {7, 9}
        } );
        Matrix b2 = JamaUtil.deleteRow( a, 1 );
        Matrix b3 = JamaUtil.deleteColumn( b2, 1 );
        assertEquals( true, JamaUtil.equals( b, b3 ) );
    }
}
