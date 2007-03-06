/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.rutherfordscattering.RSConstants;


/**
 * RSClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSClock extends SwingClock {

    /* Wall time between clock ticks (milliseconds) */
    private static final int WALL_DT = ( 1000 / RSConstants.CLOCK_FRAME_RATE );
    
    private double _defaultDt;
    private double _dt;
    
    public RSClock( double dt ) {
        super( WALL_DT, new TimingStrategy.Constant( dt ) );
        _defaultDt = _dt = dt;
    }
    
    public void setDt( final double dt ) {
        _dt = dt;
        setTimingStrategy( new TimingStrategy.Constant( dt ) );
    }
    
    public double getDt() {
        return _dt;
    }
    
    public double getDefaultDt() {
        return _defaultDt;
    }
}
