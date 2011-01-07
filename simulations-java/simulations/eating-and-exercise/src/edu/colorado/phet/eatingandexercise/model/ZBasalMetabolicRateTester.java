// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.model;

import junit.framework.TestCase;

/**
 * Created by: Sam
 * Apr 18, 2008 at 12:00:02 AM
 */
public class ZBasalMetabolicRateTester extends TestCase {
    public void testBMR() {
        //see http://en.wikipedia.org/wiki/Basal_metabolic_rate
        assertEquals( 1266, BasalMetabolicRate.getBasalMetabolicRateHarrisBenedict( 59, 1.68, EatingAndExerciseUnits.yearsToSeconds( 55 ), Human.Gender.FEMALE ), 10 );
    }
}
