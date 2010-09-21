/* Copyright 2008, University of Colorado */

package edu.colorado.phet.eatingandexercise;

import java.text.DecimalFormat;

/**
 * The collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class EatingAndExerciseStrings {
    public static String TITLE_EATING_AND_EXERCISE_MODULE = EatingAndExerciseResources.getString( "eating-and-exercise.name" );


    /* not intended for instantiation */
    private EatingAndExerciseStrings() {
    }

    public static final String UNITS_TIME = EatingAndExerciseResources.getString( "units.years" );

    public static final String KCAL = EatingAndExerciseResources.getString( "units.cal" );
    public static final String day = EatingAndExerciseResources.getString( "units.day" );
    public static final String KCAL_PER_DAY = KCAL + "/" + day;
    public static final String FATS = EatingAndExerciseResources.getString( "food.fats" );

    public static final DecimalFormat KCAL_PER_DAY_FORMAT = new DecimalFormat( "0" );
    public static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat( "0" );
    public static final DecimalFormat BMI_FORMAT = new DecimalFormat( "0.0" );
    public static final DecimalFormat AGE_FORMAT = new DecimalFormat( "0.0" );

    public static final String DISCLAIMER = EatingAndExerciseResources.getString( "disclaimer.text" );

}
