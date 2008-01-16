/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * AbstractTool is the base class for all tools in the toolbox.
 * It keeps track of its position and changes to the simulation clock.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractTool extends Movable implements ClockListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _currentTime;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AbstractTool( Point2D position ) {
        super( position );
        _currentTime = 0;
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
    
    protected double getCurrentTime() {
        return _currentTime;
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
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockPaused( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockTicked( ClockEvent clockEvent ) {}

    /*
     * Some tools require the current time to perform their calculations.
     * Tools should behave correctly even when the clock is paused, so we store the current time. 
     */
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        _currentTime = clockEvent.getSimulationTime();
        handleTimeChanged();
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
