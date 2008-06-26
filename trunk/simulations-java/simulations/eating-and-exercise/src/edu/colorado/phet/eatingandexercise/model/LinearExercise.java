package edu.colorado.phet.eatingandexercise.model;

/**
 * Created by: Sam
 * Jun 26, 2008 at 12:06:43 PM
 */
public class LinearExercise {
    private double baseCalories;
    private double beta;
    private double REFERENCE_WEIGHT = EatingAndExerciseUnits.poundsToKg( 160 );

    public double getCalories( double weight ) {
        return baseCalories * ( 1 - beta * ( weight - REFERENCE_WEIGHT ) / REFERENCE_WEIGHT );
    }

    public static void main( String[] args ) {

    }
}
