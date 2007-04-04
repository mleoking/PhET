/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import junit.framework.TestCase;

/**
 * The test suite for the class:
 */
public class ZPhetProjectVersionTester extends TestCase {
    private static final PhetProjectVersion VERSION = new PhetProjectVersion( "1", "2", "03", "1234" );

    public void testFormatForDev() {
        assertEquals( "1.2.03", VERSION.formatForDev() );
    }

    public void testFormatForProd() {
        assertEquals( "1.2", VERSION.formatForDev() );
    }

    public void testToString() {
        assertEquals( "1.2.03.1234", VERSION.toString() );
    }

    public void testAsIntMethods() {
        assertEquals( 1,    VERSION.getMajorAsInt() );
        assertEquals( 2,    VERSION.getMinorAsInt() );
        assertEquals( 3,    VERSION.getDevAsInt() );
        assertEquals( 1234, VERSION.getRevisionAsInt() );
    }

    public void testStringNeverNull() {
        PhetProjectVersion version = new PhetProjectVersion( null, null, null, null );

        assertNotNull( version.toString() );
    }
}