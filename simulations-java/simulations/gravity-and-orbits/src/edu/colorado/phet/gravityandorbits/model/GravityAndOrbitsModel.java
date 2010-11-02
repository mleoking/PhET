/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GravityAndOrbitsClock clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        
        this.clock = clock;        
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GravityAndOrbitsClock getClock() {
        return clock;
    }    
}
