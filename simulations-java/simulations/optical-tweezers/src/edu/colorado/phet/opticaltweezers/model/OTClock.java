
package edu.colorado.phet.opticaltweezers.model;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;


/**
 * OTClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTClock extends SwingClock {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final DoubleRange _dtRange;
    private double _dt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OTClock( int framesPerSecond, DoubleRange dtRange ) {
        super( 1000 / framesPerSecond, dtRange.getDefault() );
        _dtRange = dtRange;
        _dt = dtRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public DoubleRange getDtRange() {
        return _dtRange;
    }
    
    public void setDt( final double dt ) {
        System.out.println( "OTClock.setDt dt=" + dt );//XXX
        if ( dt < _dtRange.getMin() || dt > _dtRange.getMax() ) {
            throw new IllegalArgumentException( "dt is out of range: " + dt );
        }
        if ( dt != _dt ) {
            _dt = dt;
            setTimingStrategy( new TimingStrategy.Constant( dt ) );
        }
    }
    
    public double getDt() {
        return _dt;
    }
    
    public void setPaused( boolean paused ) {
        if ( paused ) {
            pause();
        }
        else {
            start();
        }
    }
}
