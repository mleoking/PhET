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
    
    private ILiquid _liquid;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleModel() {
        super();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLiquid( ILiquid liquid ) {
        _liquid = liquid;
    }
    
    public ILiquid getLiquid() {
        return _liquid;
    }
}
