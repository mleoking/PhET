/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
    
    private boolean _dragging;
    private ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AbstractTool( Point2D position ) {
        super( position );
        _dragging = false;
        _listeners = new ArrayList();
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
    
    public void setDragging( boolean dragging ) {
        if ( dragging != _dragging ) {
            _dragging = dragging;
            notifyDraggingChanged();
        }
    }
    
    public boolean isDragging() {
        return _dragging;
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public interface ToolListener {
        public void draggingChanged();
    }
    
    public void addToolListener( ToolListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeToolListener( ToolListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification handlers
    //----------------------------------------------------------------------------
    
    private void notifyDraggingChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ToolListener) i.next() ).draggingChanged();
        }
    }
    
    /**
     * Subclasses should override this if they care about position changes.
     */
    protected void handlePositionChanged() {};
    
    //----------------------------------------------------------------------------
    // ClockListener - default does nothing
    //----------------------------------------------------------------------------
    
    public void clockPaused( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockTicked( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
