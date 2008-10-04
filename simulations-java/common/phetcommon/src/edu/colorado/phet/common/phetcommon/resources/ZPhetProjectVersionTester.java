/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.resources;

import junit.framework.TestCase;

/**
 * The test suite for PhetVersionInfo.
 */
public class ZPhetProjectVersionTester extends TestCase {

    private static final PhetVersion VERSION_PUBLIC = new PhetVersion( "1", "02", "00", "1234" );
    private static final PhetVersion VERSION_DEV = new PhetVersion( "1", "02", "03", "1234" );

    public void testFormatForTitleBar() {
        assertEquals( "1.02", VERSION_PUBLIC.formatForTitleBar() );
        assertEquals( "1.02.03", VERSION_DEV.formatForTitleBar() );
    }

    public void testFormatForAboutDialog() {
        assertEquals( "1.02.00 (1234)", VERSION_PUBLIC.formatForAboutDialog() );
        assertEquals( "1.02.03 (1234)", VERSION_DEV.formatForAboutDialog() );
    }

    public void testToString() {
        assertEquals( "1.02.00 (1234)", VERSION_PUBLIC.toString() );
        assertEquals( "1.02.03 (1234)", VERSION_DEV.toString() );
    }

    public void testAsIntMethods() {
        assertEquals( 1, VERSION_DEV.getMajorAsInt() );
        assertEquals( 2, VERSION_DEV.getMinorAsInt() );
        assertEquals( 3, VERSION_DEV.getDevAsInt() );
        assertEquals( 1234, VERSION_DEV.getRevisionAsInt() );
    }

    public void testStringNeverNull() {
        PhetVersion version = new PhetVersion( null, null, null, null );
        assertNotNull( version.toString() );
    }
}