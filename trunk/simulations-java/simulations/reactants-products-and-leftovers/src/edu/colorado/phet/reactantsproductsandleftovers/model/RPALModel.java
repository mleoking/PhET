/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.model;


/**
 * Model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final RPALClock clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RPALModel( RPALClock clock ) {
        super();
        
        this.clock = clock;        
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public RPALClock getClock() {
        return clock;
    }    
}
