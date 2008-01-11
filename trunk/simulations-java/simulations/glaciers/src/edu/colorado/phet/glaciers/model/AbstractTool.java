package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * AbstractTool is the base class for all tools in the toolbox.
 * A tool is a movable model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractTool extends Movable implements ClockListener {
    
    private double _currentTime;
    
    public AbstractTool( Point2D position ) {
        super( position );
        _currentTime = 0;
        addMovableListener( new MovableAdapter() {
            public void positionChanged() {
                handlePositionChanged();
            }
        });
    }
    
    public double getElevation() {
        return getY();
    }
    
    protected double getCurrentTime() {
        return _currentTime;
    }
    
    /**
     * Subclasses should override this if they care about position changes.
     */
    protected void handlePositionChanged() {};
    
    /**
     * Subclasses should override this if they care about time changes.
     */
    protected void handleTimeChanged() {};
    
    public void clockPaused( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockTicked( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        _currentTime = clockEvent.getSimulationTime();
        handleTimeChanged();
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
