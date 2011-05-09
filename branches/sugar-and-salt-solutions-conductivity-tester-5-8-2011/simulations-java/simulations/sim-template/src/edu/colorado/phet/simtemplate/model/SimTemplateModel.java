// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simtemplate.model;


/**
 * Model template.
 */
public class SimTemplateModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final SimTemplateClock clock;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SimTemplateModel( SimTemplateClock clock ) {
        super();
        
        this.clock = clock;        
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public SimTemplateClock getClock() {
        return clock;
    }    
}
