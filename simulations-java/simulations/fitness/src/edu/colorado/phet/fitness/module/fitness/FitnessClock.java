package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;

/**
 * Created by: Sam
 * May 4, 2008 at 11:59:00 PM
 */
public class FitnessClock extends SwingClock {
    public FitnessClock() {
        super( FitnessDefaults.CLOCK_DELAY, new TimingStrategy.Constant( FitnessDefaults.CLOCK_DT, FitnessDefaults.SINGLE_STEP_TIME ) );
    }

    public void setDt( double clockDt ) {
        setTimingStrategy( new TimingStrategy.Constant( clockDt, FitnessDefaults.SINGLE_STEP_TIME ) );
    }
}
