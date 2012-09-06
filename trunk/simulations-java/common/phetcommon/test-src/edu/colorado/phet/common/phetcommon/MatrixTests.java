// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon;

import junit.framework.TestCase;

import edu.colorado.phet.common.phetcommon.math.Matrix3F;
import edu.colorado.phet.common.phetcommon.math.Matrix4F;

public class MatrixTests extends TestCase {
    public void testMultiplication3() {
        assertEquals( Matrix3F.rowMajor( 2, 7, 3, 9, 15, 46, -5, -3, -105 ).times( Matrix3F.rowMajor( 6, 7, 10, 35, 5, 36, 107, 2, -400 ) ),
                      Matrix3F.rowMajor( 578, 55, -928, 5501, 230, -17770, -11370, -260, 41842 ) );
    }

    public void testMultiplication4() {
        assertEquals( Matrix4F.rowMajor( 2, 7, 3, 50, 9, 15, 46, 15, -5, -3, -105, -17, -7, -13, -41, -93 ).times( Matrix4F.rowMajor( 6, 7, 10, -13, 35, 5, 36, 101, 107, 2, -400, 144, 9, 16, 25, 36 ) ),
                      Matrix4F.rowMajor( 1028, 855, 322, 2913, 5636, 470, -17395, 8562, -11523, -532,
                                         41417, -15970, -5721, -1684, 13537, -10474 ) );
    }
}
