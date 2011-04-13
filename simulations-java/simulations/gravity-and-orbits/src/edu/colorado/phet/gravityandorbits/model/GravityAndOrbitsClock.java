// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

//REVIEW boilerplate javadoc is incomplete, doesn't mention time scaling (presumably because we have a sim speed slider) or the significance of stepping property.

/**
 * The clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 */
public class GravityAndOrbitsClock extends ConstantDtClock {
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double DAYS_PER_TICK = 1;
    public static final int SECONDS_PER_DAY = 86400;
    public static final double DEFAULT_DT = DAYS_PER_TICK * SECONDS_PER_DAY;
    private final Property<Boolean> stepping;

    //REVIEW inconsistent naming convention for Property parameters
    public GravityAndOrbitsClock( final double baseDTValue,//multiplied by scale to obtain true dt
                                  Property<Boolean> stepping, final Property<Double> timeSpeedScaleProperty ) {
        super( 1000 / CLOCK_FRAME_RATE, baseDTValue * timeSpeedScaleProperty.getValue() );
        this.stepping = stepping;
        timeSpeedScaleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setDt( baseDTValue * timeSpeedScaleProperty.getValue() );
            }
        } );
    }

    @Override
    public void stepClockWhilePaused() {
        //REVIEW comment is bogus, no such thing as RewindProperty
        stepping.setValue( true );//See RewindProperty, it has to know whether the clock is running, paused, stepping, rewinding for application specific logic
        super.stepClockWhilePaused();
        stepping.setValue( false );
    }

    @Override
    public void stepClockBackWhilePaused() {
        stepping.setValue( true );
        super.stepClockBackWhilePaused();
        stepping.setValue( false );
    }
}
