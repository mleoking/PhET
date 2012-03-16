// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.model;

/**
 * Created by: Sam
 * Apr 17, 2008 at 11:47:59 PM
 */
public class BasalMetabolicRate {
    public static double getBasalMetabolicRateHarrisBenedict( double weight, double height, double age, Human.Gender gender ) {
        double w = weight;
        double s = height * 100;//m to cm
        double a = EatingAndExerciseUnits.secondsToYears( age );
        if ( gender == Human.Gender.MALE ) {
            return 66.4730 + 13.7516 * w + 5.0033 * s - 6.7550 * a;
        }
        else {
            return 655.0955 + 9.5634 * w + 1.8496 * s - 4.6756 * a;
        }
    }

    public static double getBasalMetabolicRateMifflinJeor( double weight, double height, double age, Human.Gender gender ) {
        double w = weight;
        double s = height * 100;//m to cm
        double a = EatingAndExerciseUnits.secondsToYears( age );
        return 9.99 * w + 6.25 * s - 4.92 * a + 166 * ( gender == Human.Gender.MALE ? 1 : 0 ) - 161;
    }
}
