/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import junit.framework.TestCase;

/**
 * The test suite for the class:
 */
public class ZPhetProjectVersionTester extends TestCase {
    public void testFormatForDev() {
        PhetProjectVersion version = new PhetProjectVersion( "1", "2", "03", "1234" );

        assertEquals("1.2.03", version.formatForDev());
    }

    public void testFormatForProd() {
        PhetProjectVersion version = new PhetProjectVersion( "1", "2", "03", "1234" );

        assertEquals("1.2", version.formatForDev());
    }


    public void testToString() {
        PhetProjectVersion version = new PhetProjectVersion( "1", "2", "03", "1234" );

        assertEquals("1.2.03.1234", version.toString());
    }

    public void testStringNeverNull() {
        PhetProjectVersion version = new PhetProjectVersion( null, null, null, null );

        assertNotNull(version.toString());
    }
}