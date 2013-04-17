// Copyright 2002-2011, University of Colorado

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
    private boolean _deletedSelf;
    private final ArrayList _listeners;
    
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
    
    protected void deleteSelf() {
        if ( !_deletedSelf ) {
            _deletedSelf = true;
            notifyDeleteMe();
        }
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
            if ( !dragging ) {
                constrainDrop();
            }
        }
    }
    
    /**
     * Called when dragging had ended, and tool has been dropped.
     * Default implementation is to do nothing.
     * Subclasses that have drop constraints should override this method. 
     */
    protected void constrainDrop() {}
    
    public boolean isDragging() {
        return _dragging;
    }
    
    protected boolean isDeletedSelf() {
        return _deletedSelf;
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public interface ToolListener {
        public void draggingChanged();
        public void deleteMe( AbstractTool tool );
    }
    
    public static class ToolAdapter implements ToolListener {
        public void draggingChanged() {}
        public void deleteMe( AbstractTool tool ) {}
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
    
    private void notifyDeleteMe() {
        ArrayList listenersCopy = new ArrayList( _listeners ); // iterate on a copy to avoid ConcurrentModificationException
        Iterator i = listenersCopy.iterator();
        while ( i.hasNext() ) {
            ( (ToolListener) i.next() ).deleteMe( this );
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
