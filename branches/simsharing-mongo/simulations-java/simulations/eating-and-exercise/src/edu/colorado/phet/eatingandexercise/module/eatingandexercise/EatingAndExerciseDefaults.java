// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;

/**
 * Created by: Sam
 * Apr 18, 2008 at 12:41:19 AM
 */
public class EatingAndExerciseDefaults {
    public static final double CLOCK_DT = 1000.0 * 250 / 2;
    //    public static final int CLOCK_DELAY = 30;
    public static final int CLOCK_DELAY = 30 * 3 * 2;
    public static final boolean STARTS_PAUSED = true;
    public static final double SINGLE_STEP_TIME = EatingAndExerciseUnits.monthsToSeconds( 1 );
}
