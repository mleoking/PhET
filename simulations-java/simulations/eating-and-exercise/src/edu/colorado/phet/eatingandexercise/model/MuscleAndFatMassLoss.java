package edu.colorado.phet.eatingandexercise.model;

import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:12:19 PM
 */
public class MuscleAndFatMassLoss implements HumanUpdate {
    public void update( Human human, double dt ) {

        double originalBodyFatPercent = human.getFatMassPercent();
        double originalMass = human.getMass();

        double caloriesGainedPerDay = human.getDeltaCaloriesGainedPerDay();
//        double kgGainedPerDay = EatingAndExerciseUnits.caloriesToKG( caloriesGainedPerDay );

        double caloriesGained = caloriesGainedPerDay * EatingAndExerciseUnits.secondsToDays( dt );

        if ( caloriesGained < 0 ) {//losing weight
            double caloriesBurned = -caloriesGained;
            //free parameters:
            // a) LBM + FAT mass
            // b) BodyFatPercent + total mass

//        >>>Model for weight loss:
//
//	LBM_lost (g) = 0.12 * 4 * Cal_burned
//	Fat_lost (g) = 0.88 * 9 * Cal_burned
//
//>>>Starvation mode:
//	When starving (<2%/4% for men/women) make the ratio switch, with buffering as follows:
//		%fat 2-4% (men), 4-6% (women): 	LBM loss 50%, Fat loss 50%
//		%fat < 2% (men), < 4% (women): 	LBM loss 95%, Fat loss 5%. (this might become 100% LBM...not yet sure).


            double LBM = human.getLeanBodyMass();
            double fatMass = human.getFatMass();

            double deltaLBM = -0.12 / 4 * caloriesBurned / 1000;
            double deltaFatMass = -0.88 / 9 * caloriesBurned / 1000;

            double deltaMass = deltaLBM + deltaFatMass;
            double expectedDeltaMass = EatingAndExerciseUnits.caloriesToKG( caloriesGained );
            System.out.println( "deltaMass = " + deltaMass + ", expected: " + expectedDeltaMass );

            double newLBM = deltaLBM + LBM;
            double newFatMass = deltaFatMass + fatMass;

            if ( newFatMass < 0 ) {
                System.out.println( "new fat mass was negative" );
                double massThatShouldHaveComeFromFatButCouldntBecauseFatIsDepleted = -newFatMass;
                newFatMass = 1E-6;

                newLBM = newLBM - massThatShouldHaveComeFromFatButCouldntBecauseFatIsDepleted;
                if ( newLBM < 0 ) {
                    newLBM = 1E-6;
                }
            }

            double newMass = newLBM + fatMass;
            double newFatPercent = newFatMass / newMass * 100.0;

            human.setMass( newMass );
            human.setFatMassPercent( newFatPercent );
            human.getMassVariable().addValue( human.getMass(), human.getAge() );
        }
        else {
            human.setMass( human.getMass() + EatingAndExerciseUnits.caloriesToKG( caloriesGained ) );
            human.getMassVariable().addValue( human.getMass(), human.getAge() );
        }
    }

    public static void main( String[] args ) {
        MuscleAndFatMassLoss muscleAndFatMassLoss = new MuscleAndFatMassLoss();
        Human h = new Human();
        System.out.println( "  expected weight gain in one day "
                            + EatingAndExerciseUnits.caloriesToKG( h.getDeltaCaloriesGainedPerDay() ) );
        double mass = h.getMass();
        muscleAndFatMassLoss.update( h, EatingAndExerciseUnits.daysToSeconds( 1 ) );
        double finalMass = h.getMass();
//        System.out.println( "mass = " + mass+",finalMass" );
        double delta = finalMass - mass;
        System.out.println( "delta = " + delta );
        h.getSelectedFoods().addItem( new CaloricFoodItem( "Asparagus", "", 10, 10, 10 ) );
        System.out.println( "  h.getDailyCaloricIntake() = " + h.getDailyCaloricIntake() );

    }

}
