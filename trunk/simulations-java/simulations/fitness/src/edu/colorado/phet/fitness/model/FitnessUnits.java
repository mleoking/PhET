package edu.colorado.phet.fitness.model;

/**
 * Created by: Sam
 * Apr 17, 2008 at 11:48:34 PM
 */
public class FitnessUnits {
    private static double SECONDS_PER_YEAR = 365 * 24 * 60 * 60;
    private static final double SECONDS_PER_DAY = 24 * 60 * 60;
    private static double POUNDS_PER_KG = 2.20462262;

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
        return years * SECONDS_PER_YEAR;
    }

    public static double secondsToDays( double seconds ) {
        return seconds / SECONDS_PER_DAY;
    }

    public static double feetToMeters( double dist ) {
        return dist * 0.3048;
    }

    public static double metersToFeet( double dist ) {
        return dist / 0.3048;
    }

    public static double gramsToCaloriesCarb( double grams ) {
        return grams * 4;
    }

    public static double gramsToCaloriesProtein( double grams ) {
        return grams * 4;
    }

    public static double gramsToCaloriesLipids( double grams ) {
        return grams * 9;
    }

    public static double kgToPounds( double mass ) {
        return mass * POUNDS_PER_KG;
    }

    public static double poundsToKg( double value ) {
        return value / POUNDS_PER_KG;
    }

}
