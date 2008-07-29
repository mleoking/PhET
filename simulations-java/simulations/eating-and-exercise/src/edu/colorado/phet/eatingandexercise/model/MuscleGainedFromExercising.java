package edu.colorado.phet.eatingandexercise.model;

/**
 * Created by: Sam
 * Jul 17, 2008 at 11:44:06 AM
 */
public class MuscleGainedFromExercising implements HumanUpdate {
    private static boolean debug = false;

    /**
     * >>>Model for muscle building from exercise:
     * <p/>
     * Muscle_mass_gained = 0.1 * Cal_exercise * (LBM_0 - LBM)
     * <p/>
     * ...where LBM is lean body mass, LBM_0 is 0.96 * sqrt(height / 30),
     * or the lean body mass of a person with 4% body fat and a BMI of 30.
     * <p/>
     * >>>These two features above will effectively make BMR vary according to
     * your caloric intake (if you undereat, your BMR should drop some to compensate, vice versa for eating more and exercising).
     * <p/>
     * >>> all other weight gained is added fat mass.
     * <p/>
     * Wendy said:
     * The equations below for muscle gained due to exercise still have the
     * algebra error.  LBM_O = W *96% = BMI*h2 * 96%  You also don't mention this
     * is for men and that women would be 91% and BMI 26 I think you found.  I also
     * don't see the muscle mass loss due to lack of exercise.
     *
     * @param human
     * @param dt
     */
    public void update( Human human, double dt ) {
//        double calExercise = human.getCaloriesExerciseAndActivityPerDay() * EatingAndExerciseUnits.secondsToDays( dt );
        double calExercise = human.getCaloriesExercisePerDay() * EatingAndExerciseUnits.secondsToDays( dt );
        println( "Calories exercise: " + calExercise );
//        double percentFat = human.getGender().getStdPercentFat();
        double stdBMI = human.getGender().getStdBMI();
        println( "stdBMI = " + stdBMI );
        double stdLeanMassFraction = human.getGender().getStdLeanMassFraction();
        println( "stdLeanMassFraction = " + stdLeanMassFraction );
        double LBM_0 = stdBMI * human.getHeight() * human.getHeight() * stdLeanMassFraction;//todo: should use standard height instead of actual human instance height?
        println( "LBM_0 = human.getGender().getStdBMI() * human.getHeight() * human.getHeight() * human.getGender().getStdLeanMassFraction() =" + LBM_0 + " kg" );
        double muscleMassGained = 0.02 * calExercise * ( LBM_0 - human.getLeanBodyMass() ) / 4000.0;
        println( "Human lean body mass=" + human.getLeanBodyMass() );
        println( "muscleMassGained= 0.1 * calExercise * ( LBM_0 - human.getLeanBodyMass() )/4000.0 = " + muscleMassGained + " kg" );
        double origMass = human.getMass();
        double newLeanMass = muscleMassGained + human.getLeanBodyMass();
        double fracLean = newLeanMass / origMass;
        human.setLeanFraction( fracLean );
    }

    public static void main( String[] args ) {
        Human human = new Human();
        System.out.println( "Before" );
        System.out.println( "human.getFatMassPercent() = " + human.getFatMassPercent() );
        System.out.println( "human.getMass() = " + human.getMass() );
        new MuscleGainedFromExercising().update( human, EatingAndExerciseUnits.daysToSeconds( 1 ) );
        System.out.println( "After:" );
        System.out.println( "human.getFatMassPercent() = " + human.getFatMassPercent() );
        System.out.println( "human.getMass() = " + human.getMass() );
    }

    private static void println( String s ) {
        if ( debug ) {
            System.out.println( s );
        }
    }
}
