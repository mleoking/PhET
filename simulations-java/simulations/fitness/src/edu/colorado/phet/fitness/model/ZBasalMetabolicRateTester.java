package edu.colorado.phet.fitness.model;

import junit.framework.TestCase;

/**
 * Created by: Sam
 * Apr 18, 2008 at 12:00:02 AM
 */
public class ZBasalMetabolicRateTester extends TestCase {
    public void testBMR() {
        //see http://en.wikipedia.org/wiki/Basal_metabolic_rate
        assertEquals( 1266, BasalMetabolicRate.getBasalMetabolicRateHarrisBenedict( 59, 1.68, FitnessUnits.yearsToSeconds( 55 ), Human.Gender.FEMALE ), 10 );
//        assertEquals( 1266, BasalMetabolicRate.getBasalMetabolicRateMifflinJeor( 59, 1.68, FitnessUnits.yearsToSeconds( 55 ), Human.Gender.FEMALE ), 1E-6 );
    }
}
