/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * Base class for all models.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ABSClock clock;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ABSModel( ABSClock clock ) {
        this.clock = clock;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public ABSClock getClock() {
        return clock;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    public void addClockListener( ClockListener clockListener ) {
        clock.addClockListener( clockListener );
    }
    
    public void removeClockListener( ClockListener clockListener ) {
        clock.removeClockListener( clockListener );
    }
}
