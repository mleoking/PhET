/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;


/**
 * Model template.
 */
public class BuildAnAtomModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final BuildAnAtomClock clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();
        
        this.clock = clock;        
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BuildAnAtomClock getClock() {
        return clock;
    }    
}
