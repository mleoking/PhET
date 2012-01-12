// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module;

import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * Base class for all models.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AABSClock clock;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AABSModel( AABSClock clock ) {
        this.clock = clock;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public AABSClock getClock() {
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
