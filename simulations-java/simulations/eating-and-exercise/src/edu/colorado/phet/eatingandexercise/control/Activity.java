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
    public static final Activity DEFAULT_ACTIVITY_LEVEL = new Activity( EatingAndExerciseResources.getString( "activity.moderate" ), 1.5 );

    public static final Activity[] DEFAULT_ACTIVITY_LEVELS = {
            new Activity( EatingAndExerciseResources.getString( "activity.very-sedentary" ), 1.3 ),
            new Activity( EatingAndExerciseResources.getString( "activity.sedentary" ), 1.4 ),
            DEFAULT_ACTIVITY_LEVEL,
            new Activity( EatingAndExerciseResources.getString( "activity.active" ), 1.6 ),
            new Activity( EatingAndExerciseResources.getString( "activity.athletic" ), 1.7 )
    };


    public Activity( String name, double activityLevel ) {
        this.name = name;
        this.activityLevel = activityLevel;
    }

    public String toString() {
        return name;
    }

    public double getValue() {
        return activityLevel - 1;
    }
}
