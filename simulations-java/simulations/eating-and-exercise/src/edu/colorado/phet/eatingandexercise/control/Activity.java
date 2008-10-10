package edu.colorado.phet.eatingandexercise.control;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;

/**
 * Created by: Sam
 * Apr 24, 2008 at 2:43:05 AM
 * <p/>
 * test comment
 */
public class Activity {
    private String name;
    private double activityLevel;
    private double BMI_0;
    public static final Activity DEFAULT_ACTIVITY_LEVEL = new Activity( EatingAndExerciseResources.getString( "activity.moderate" ), 0.45, 22.5 );

    /*
    BMI_0
    18.5 (very sedentary)
		20.0 (sedentary)
		22.5 (moderately active)
		25.0 (active)
     */

    public static final Activity[] DEFAULT_ACTIVITY_LEVELS = {
            new Activity( EatingAndExerciseResources.getString( "activity.very-sedentary" ), 0.05, 18.5 ),
            new Activity( EatingAndExerciseResources.getString( "activity.sedentary" ), 0.15, 20 ),
            DEFAULT_ACTIVITY_LEVEL,
            new Activity( EatingAndExerciseResources.getString( "activity.active" ), 0.75, 25 ),
    };


    public Activity( String name, double activityLevel, double BMI_0 ) {
        this.name = name;
        this.activityLevel = activityLevel;
        this.BMI_0 = BMI_0;
    }

    public String toString() {
        return name;
    }

    public double getBMI_0() {
        return BMI_0;
    }

    public double getValue() {
        return activityLevel;
    }
}
