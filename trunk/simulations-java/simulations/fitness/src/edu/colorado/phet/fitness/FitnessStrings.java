/* Copyright 2008, University of Colorado */

package edu.colorado.phet.fitness;

import java.text.DecimalFormat;

/**
 * The collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class FitnessStrings {
    public static String TITLE_FITNESS_MODULE = FitnessResources.getString( "fitness.name" );


    /* not intended for instantiation */
    private FitnessStrings() {
    }

    public static final String UNITS_TIME = FitnessResources.getString( "units.years" );

    public static final String KCAL = FitnessResources.getString( "units.cal" );
    public static final String day= FitnessResources.getString( "units.day" );
    public static final String KCAL_PER_DAY = KCAL + "/"+day;
    public static final String FATS = FitnessResources.getString( "food.fats" );

    public static final DecimalFormat KCAL_PER_DAY_FORMAT = new DecimalFormat( "0" );
    public static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat( "0" );
    public static final DecimalFormat AGE_FORMAT = new DecimalFormat( "0.0" );

    public static final String DISCLAIMER = FitnessResources.getString( "disclaimer.text" );

}
