package edu.colorado.phet.eatingandexercise.model;

import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:12:19 PM
 */
public class MuscleAndFatMassLoss2 implements HumanUpdate {

    public static double FRACTION_FAT_LOST = 0.5;
    public static boolean allFatWhenGainingWeight = true;

    public void update( Human human, double dt ) {

        double caloriesGainedPerDay = human.getDeltaCaloriesGainedPerDay();
        double caloriesGained = caloriesGainedPerDay * EatingAndExerciseUnits.secondsToDays( dt );

        double fractionFatLost = FRACTION_FAT_LOST;
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
        double totalKGLost = (fractionFatLost / 9000 + (1 - fractionFatLost) / 4000) * caloriesLost;
        double kgFatLost = totalKGLost * fractionFatLost;
        double newMass = human.getMass() - totalKGLost;
        if ( newMass <= 0 ) {
            newMass = 0;
        }
        double newFatMass = human.getFatMass() - kgFatLost;
        if ( newFatMass <= 0 ) {//todo: better corner case handling
            newFatMass = 0;
        }
        double newFatMassPercent = newFatMass / newMass * 100.0;
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