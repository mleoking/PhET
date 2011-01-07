// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;

/**
 * Created by: Sam
 * Apr 18, 2008 at 9:39:53 AM
 */
public class Exercise {
    private String name;
    private double calories;

    public Exercise( String name, double caloriesBurned ) {
        this.name = name;
        this.calories = caloriesBurned;
    }

    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public String toString() {
        String calPerDay = EatingAndExerciseResources.getString( "units.cal-per-day" );
        return name + ": " + calories + " " + calPerDay;
    }
}
