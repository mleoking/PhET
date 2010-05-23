package edu.colorado.phet.website.tests;

import org.junit.Test;

import edu.colorado.phet.website.data.Changelog;

import static org.junit.Assert.assertTrue;

public class ChangelogTests {
    @Test
    public void testCorrectFormat() {
        String logString = "# 3.22.00 (38666) Feb 16, 2010\n" +
                           "2/16/10 Deployment to production server.\n" +
                           "# 3.21.01 (38614) Feb 12, 2010\n" +
                           "2/12/10 > Fixed an issue where U-238 was shown decaying into Pb-207 instead of Pb-206.\n" +
                           "# 3.21.00 (38193) Jan 22, 2010\n" +
                           "1/22/10 Deployment to production server.";

        Changelog devLog = new Changelog( logString );

        System.out.println( "*** dev log: \n" + devLog + "\n\n" );

        Changelog log = devLog.getNonDevChangelog();

        System.out.println( "*** non-dev log: \n" + log + "\n\n" );

        /*---------------------------------------------------------------------------*
        * development log tests
        *----------------------------------------------------------------------------*/

        assertTrue( devLog.getEntries().size() == 3 );
        Changelog.Entry dev1 = devLog.getEntries().get( 0 );
        assertTrue( dev1.getMajorVersion() == 3 );
        assertTrue( dev1.getDevVersion() == 0 );
        assertTrue( dev1.getRevision() == 38666 );
        assertTrue( dev1.getDate() != null );
        assertTrue( dev1.getLines().size() == 1 );
        assertTrue( !dev1.getLines().get( 0 ).isVisible() );
        assertTrue( dev1.getLines().get( 0 ).getMessage().equals( "Deployment to production server." ) );
        Changelog.Entry dev2 = devLog.getEntries().get( 1 );
        assertTrue( dev2.getLines().size() == 1 );
        Changelog.Line devE = dev2.getLines().get( 0 );
        assertTrue( devE.isVisible() );
        assertTrue( devE.getDate() != null );
        assertTrue( devE.getMessage().equals( "Fixed an issue where U-238 was shown decaying into Pb-207 instead of Pb-206." ) );

        /*---------------------------------------------------------------------------*
        * visible log tests
        *----------------------------------------------------------------------------*/

        assertTrue( log.getEntries().size() == 2 );
        Changelog.Entry e1 = devLog.getEntries().get( 0 );
        assertTrue( e1.getMajorVersion() == 3 );
        assertTrue( e1.getDevVersion() == 0 );
        assertTrue( e1.getRevision() == 38666 );
        assertTrue( e1.getDate() != null );
        assertTrue( e1.getLines().size() == 1 );
        for ( Changelog.Entry entry : log.getEntries() ) {
            assertTrue( entry.getDevVersion() == 0 );
            for ( Changelog.Line line : entry.getLines() ) {
                assertTrue( line.isVisible() );
            }
        }

    }
}