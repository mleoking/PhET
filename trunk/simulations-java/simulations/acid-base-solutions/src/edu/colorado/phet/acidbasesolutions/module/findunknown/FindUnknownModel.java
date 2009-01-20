/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.findunknown;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;

/**
 * FindUnknownModel is the model for FindUnknownModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindUnknownModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ABSClock _clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FindUnknownModel( ABSClock clock ) {
        super();
        
        _clock = clock;

        //XXX other stuff
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ABSClock getClock() {
        return _clock;
    }
}
