// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;

/**
 * Created by: Sam
 * May 4, 2008 at 11:59:00 PM
 */
public class EatingAndExerciseClock extends SwingClock {
    public EatingAndExerciseClock() {
        super( EatingAndExerciseDefaults.CLOCK_DELAY, new TimingStrategy.Constant( EatingAndExerciseDefaults.CLOCK_DT, EatingAndExerciseDefaults.SINGLE_STEP_TIME ) );
    }

    public void setDt( double clockDt ) {
        setTimingStrategy( new TimingStrategy.Constant( clockDt, EatingAndExerciseDefaults.SINGLE_STEP_TIME ) );
    }
}
