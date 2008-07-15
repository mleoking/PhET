package edu.colorado.phet.eatingandexercise.model;

import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:12:19 PM
 */
public class MuscleAndFatMassLoss2 implements HumanUpdate {
    public void update( Human human, double dt ) {

        double caloriesGainedPerDay = human.getDeltaCaloriesGainedPerDay();
        double caloriesGained = caloriesGainedPerDay * EatingAndExerciseUnits.secondsToDays( dt );

        if ( caloriesGained < 0 ) {//losing weight
            double caloriesLost = -caloriesGained;
            double totalKGLost = EatingAndExerciseUnits.caloriesToKG( caloriesLost );
            double kgFatLost = totalKGLost * 0.88;
            double kgLBMLost = totalKGLost * 0.12;
            double newMass = human.getMass() - totalKGLost;
            double newFatMass = human.getFatMass() - kgFatLost;
            double newFatMassPercent = newFatMass / newMass * 100.0;
            updateMass( human, newMass );
            human.setFatMassPercent( newFatMassPercent );
        }
        else {
            //gain weight at constant body fat %
            double m = human.getMass() + EatingAndExerciseUnits.caloriesToKG( caloriesGained );
            updateMass( human, m );
        }
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