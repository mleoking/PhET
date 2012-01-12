// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.util;

import junit.framework.TestCase;

/**
 * The test suite for the class:
 */
public class ZStringUtilTester extends TestCase {
    public void setUp() {
    }

    public void tearDown() {
    }

    public void testAsIntForInt() {
        assertEquals( 4, StringUtil.asInt( "4", 0 ) );
    }

    public void testAsIntForNonInt() {
        assertEquals( 4, StringUtil.asInt( "a", 4 ) );
    }

    public void testAsDoubleForDouble() {
        assertEquals( 3.1415, StringUtil.asDouble( "a", 3.1415 ), 0.0 );
    }

    public void testAsDoubleForNonDouble() {
        assertEquals( 3.1415, StringUtil.asDouble( "a", 3.1415 ), 0.0 );
    }

    public void testAsCharForChar() {
        assertEquals( 'a', StringUtil.asChar( "a", 'b' ) );
    }

    public void testAsCharForNonChar() {
        assertEquals( 'a', StringUtil.asChar( "bc", 'a' ) );
    }
}