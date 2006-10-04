/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;

/**
 * MovingObject is any object that has a position and direction of motion.
 * Both the position and direction are mutable.
 * Observers receive notification when the position or direction properties change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MovingObject extends StationaryObject implements ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /** Property used to notify observers of a direction change */
    public static final String PROPERTY_DIRECTION = "direction";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _direction; // direction of motion, in radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MovingObject() {
        this( new Point2D.Double( 0, 0 ), 0 );
    }
    
    public MovingObject( Point2D position, double direction ) {
        super( position );
        _direction = direction;
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
    public double getDirection() {
        return _direction;
    }
    
    public void setDirection( double direction ) {
        if ( direction != _direction) {
            _direction = direction;
            notifyObservers( PROPERTY_DIRECTION );
        }
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation - does nothing by default
    //----------------------------------------------------------------------------
    
    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
