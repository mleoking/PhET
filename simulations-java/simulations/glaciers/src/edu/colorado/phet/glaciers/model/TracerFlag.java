/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * TracerFlag is the model of a tracer flag.
 * A tracer flag can be planted at a position along the glacier.
 * It will move with the glacier, thus indicating glacier movement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracerFlag extends AbstractTool {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlag( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    public void cleanup() {
        super.cleanup();
    }

    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    //XXX should this be replaced with a GlacierListener?
    protected void handleTimeChanged() {
        //XXX calculate new position, call setPosition
    }

}
