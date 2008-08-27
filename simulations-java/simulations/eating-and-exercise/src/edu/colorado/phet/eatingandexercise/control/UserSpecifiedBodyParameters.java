package edu.colorado.phet.eatingandexercise.control;

import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jul 17, 2008 at 9:06:12 PM
 */
public class UserSpecifiedBodyParameters {

    /*
       /*We will have a button near the % fat slider that says "Auto" or something like that.
       When you click it, it automatically calculates a % fat based on activity, height, and weight.
    */
    public double getAutoFatMassPercent( Human human ) {
        //see http://www.halls.md/bmi/fat.htm
        //Adult Body Fat % = (1.20 x BMI) + (0.23 x Age) - (10.8 x gender) - 5.4
//where male gender= 1, female=0.
        //return 1.2 * human.getBMI() + 0.23 * EatingAndExerciseUnits.secondsToYears( human.getAge() ) - 10.8 * ( human.getGender() == Human.Gender.MALE ? 1 : 0 ) - 5.4;
        //Changed 8/27/08 based on feedback from Wendy
        return 1.4 * human.getBMI() - 8.0 * ( human.getGender() == Human.Gender.MALE ? 1 : 0 ) - 9.0;
    }

    public static void main( String[] args ) {
        Human human = new Human();
        double pref = new UserSpecifiedBodyParameters().getAutoFatMassPercent( human );
        System.out.println( "pref = " + pref );
    }
}
