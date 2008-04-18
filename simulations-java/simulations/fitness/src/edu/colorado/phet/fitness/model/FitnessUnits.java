package edu.colorado.phet.fitness.model;

/**
 * Created by: Sam
 * Apr 17, 2008 at 11:48:34 PM
 */
public class FitnessUnits {
    private static double SECONDS_PER_YEAR = 365 * 24 * 60 * 60;

    public static double secondsToYears( double sec ) {
        return sec / SECONDS_PER_YEAR;
    }

    public static double caloriesToKG( double caloriesGained ) {
        return poundsToKG( caloriesToPounds( caloriesGained ) );
    }

    private static double poundsToKG( double pounds ) {
        //1 pound = 0.45359237 kilograms
        return pounds * 0.45359237;
    }

    private static double caloriesToPounds( double calories ) {
        return calories / 3500;
    }

    public static void main( String[] args ) {
        System.out.println( "SECONDS_PER_YEAR = " + SECONDS_PER_YEAR );
    }

    public static double yearsToSeconds( double years ) {
        return years*SECONDS_PER_YEAR;
    }
}
