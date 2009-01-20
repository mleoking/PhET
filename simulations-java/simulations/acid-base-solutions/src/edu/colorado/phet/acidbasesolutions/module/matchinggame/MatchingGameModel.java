/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;

/**
 * MatchingGameModel is the model for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ABSClock _clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MatchingGameModel( ABSClock clock ) {
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
