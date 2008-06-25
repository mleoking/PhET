/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

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

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleModel( IClock clock ) {
        super();
        _liquid = new Liquid( LiquidDescriptor.LEMON_JUICE );
        clock.addClockListener( _liquid );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Liquid getLiquid() {
        return _liquid;
    }
}
