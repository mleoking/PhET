package edu.colorado.phet.website.tests;

import org.junit.Test;

import edu.colorado.phet.website.data.Changelog;
import edu.colorado.phet.website.util.StringUtils;

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

        assertTrue( devLog.getEntries().size() == 3 );
        assertTrue( log.getEntries().size() == 2 );

    }
}