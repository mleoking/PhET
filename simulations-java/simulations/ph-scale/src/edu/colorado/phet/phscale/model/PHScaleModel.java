/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

/**
 * PHScaleModel is the model for PHScaleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final Beaker _beaker;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleModel() {
        super();
        _liquid = new Liquid( LiquidDescriptor.LEMON_JUICE, 1 );
        _beaker = new Beaker();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Liquid getLiquid() {
        return _liquid;
    }
    
    public Beaker getBeaker() {
        return _beaker;
    }
}
