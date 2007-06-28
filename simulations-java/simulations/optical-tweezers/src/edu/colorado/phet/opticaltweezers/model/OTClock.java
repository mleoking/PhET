
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
    
    private final DoubleRange _slowRange;
    private final DoubleRange _fastRange;
    private double _dt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OTClock( int framesPerSecond, DoubleRange slowRange, DoubleRange fastRange, double dt ) {
        super( 1000 / framesPerSecond, dt );
        
        if ( slowRange.getMax() > fastRange.getMin() ) {
            throw new IllegalArgumentException( "slowRange and fastRange overlap" );
        }
        if ( dt < slowRange.getMin() || dt > fastRange.getMax() ) {
            throw new IllegalArgumentException( "dt out of range: " + dt );
        }
        
        _slowRange = slowRange;
        _fastRange = fastRange;
        _dt = dt;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public DoubleRange getSlowRange() {
        return _slowRange;
    }
    
    public DoubleRange getFastRange() {
        return _fastRange;
    }
    
    public void setDt( final double dt ) {
        if ( dt < _slowRange.getMin() || dt > _fastRange.getMax() ) {
            throw new IllegalArgumentException( "dt out of range: " + dt );
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
