/* Copyright 2008, University of Colorado */

package edu.colorado.phet.fitness;

/**
 * The collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class FitnessStrings {
    public static String TITLE_FITNESS_MODULE = FitnessResources.getString( "fitness.module.title" );


    /* not intended for instantiation */
    private FitnessStrings() {
    }

    public static final String LABEL_POSITION = FitnessResources.getString( "label.position" );
    public static final String LABEL_ORIENTATION = FitnessResources.getString( "label.orientation" );

    public static final String TITLE_EXAMPLE_CONTROL_PANEL = FitnessResources.getString( "title.exampleControlPanel" );
    public static final String TITLE_EXAMPLE_MODULE = FitnessResources.getString( "title.exampleModule" );

    public static final String UNITS_DISTANCE = FitnessResources.getString( "units.distance" );
    public static final String UNITS_ORIENTATION = FitnessResources.getString( "units.orientation" );
    public static final String UNITS_TIME = FitnessResources.getString( "units.time" );

    public static final String KCAL = "Cal";
    public static final String KCAL_PER_DAY = KCAL + "/day";
    public static final String FATS = "Fats";
}
