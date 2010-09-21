package edu.colorado.phet.eatingandexercise.model;

import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:12:19 PM
 */
public class MuscleAndFatMassLoss2 implements HumanUpdate {

    public static double FRACTION_FAT_LOST = 0.5;
    public static boolean allFatWhenGainingWeight = true;
    private static final double MIN_MASS = 1E-6;

    public void update( Human human, double dt ) {

        double caloriesGainedPerDay = human.getDeltaCaloriesGainedPerDay();
        double caloriesGained = caloriesGainedPerDay * EatingAndExerciseUnits.secondsToDays( dt );

        // double fractionFatLost = FRACTION_FAT_LOST;
        // Make fractionFatLost linear function of FatMassPercent, up to FatMassPercent = 50%
        // Added by NP 8-11-08
        double fatMassPercent = human.getFatMass() / human.getMass();
        double fractionFatLost = fatMassPercent * human.getGender().getFatMassMultiplier();
        if ( fatMassPercent > human.getGender().getFatMassLimiter() ) {
            fractionFatLost = 1.0;
        }
        // end of changes to fractionFatLost by NP

        if ( human.isAlmostStarving() ) {
            fractionFatLost = 0.5;
        }
        else if ( human.isStarving() ) {
            fractionFatLost = 0.05;
        }

        // Make sure that the 88% rule is only used for losing weight.
        // If you gain weight just from taking in excess calories, it should all go to fat.
        // You should only be able to turn food and fat into muscle by exercising.

        //control for asymmetric fat percent handling when gaining weight
        if ( caloriesGained > 0 && allFatWhenGainingWeight ) {
            fractionFatLost = 1.0;
        }

        double caloriesLost = -caloriesGained;
        //   double totalKGLost = EatingAndExerciseUnits.caloriesToKG( caloriesLost );
        double totalKGLost = ( fractionFatLost / 9000 + ( 1 - fractionFatLost ) / 4000 ) * caloriesLost;
        double kgFatLost = totalKGLost * fractionFatLost;
        double newMass = human.getMass() - totalKGLost;
        if ( newMass <= MIN_MASS ) {
            newMass = MIN_MASS;
        }
        double newFatMass = human.getFatMass() - kgFatLost;
        if ( newFatMass <= MIN_MASS ) {//todo: better corner case handling
            newFatMass = MIN_MASS;
        }
        double newFatMassPercent = newFatMass / newMass * 100.0;

        if ( newMass == MIN_MASS ) {
            newFatMassPercent = fatMassPercent;//if hit zero mass, use same fat mass percent as before
        }
        updateMass( human, newMass );
        human.setFatMassPercent( newFatMassPercent );
    }

    private void updateMass( Human human, double m ) {
        human.setMass( m );
        human.getMassVariable().addValue( m, human.getAge() );
    }

    public static void main( String[] args ) {
        MuscleAndFatMassLoss2 muscleAndFatMassLoss = new MuscleAndFatMassLoss2();
        Human h = new Human();
        h.getSelectedFoods().addItem( new CaloricFoodItem( "Asparagus", "", 10, 10, 10 ) );
        System.out.println( "  expected weight gain in one day "
                            + EatingAndExerciseUnits.caloriesToKG( h.getDeltaCaloriesGainedPerDay() ) );
        double mass = h.getMass();
        muscleAndFatMassLoss.update( h, EatingAndExerciseUnits.daysToSeconds( 1 ) );
        double finalMass = h.getMass();
//        System.out.println( "mass = " + mass+",finalMass" );
        double delta = finalMass - mass;
        System.out.println( "delta = " + delta );

//        System.out.println( "  h.getDailyCaloricIntake() = " + h.getDailyCaloricIntake() );
    }

}