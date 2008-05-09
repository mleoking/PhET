/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * BoreholeDrill is the model of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrill extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrill( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    /*
     * Always snap to the ice surface.
     */
    protected void constrainDrop() {
        double surfaceElevation = _glacier.getSurfaceElevation( getX() );
        setPosition( getX(), surfaceElevation );
    }
}
