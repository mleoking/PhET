/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import edu.colorado.phet.phscale.model.Liquid.Water;



/**
 * PHScaleModel is the model for PHScaleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Liquid _liquid;
    private final Water _water;
    private final Beaker _beaker;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleModel() {
        super();
        _liquid = Liquid.LEMON_JUICE;
        _water = Liquid.WATER;
        _beaker = new Beaker();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Liquid getLiquid() {
        return _liquid;
    }
    
    public Water getWater() {
        return _water;
    }
    
    public Beaker getBeaker() {
        return _beaker;
    }
}
