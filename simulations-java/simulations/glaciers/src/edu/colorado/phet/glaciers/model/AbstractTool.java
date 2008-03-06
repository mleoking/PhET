/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * AbstractTool is the base class for all tools in the toolbox.
 * It keeps track of its position and changes to the simulation clock.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractTool extends Movable {
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AbstractTool( Point2D position ) {
        super( position );
        addMovableListener( new MovableAdapter() {
            public void positionChanged() {
                handlePositionChanged();
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getElevation() {
        return getY();
    }
    
    //----------------------------------------------------------------------------
    // Notification handlers
    //----------------------------------------------------------------------------
    
    /**
     * Subclasses should override this if they care about position changes.
     */
    protected void handlePositionChanged() {};
    
    /**
     * Subclasses should override this if they care about time changes.
     */
    protected void handleTimeChanged() {};
}
