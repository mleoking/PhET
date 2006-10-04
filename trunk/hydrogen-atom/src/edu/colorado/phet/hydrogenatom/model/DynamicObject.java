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
 * DynamicObject
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DynamicObject extends HAObservable implements ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ALIVE = "alive";
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_ORIENTATION = "orientation";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private long _id; // unique identifier
    private boolean _isAlive; // is the object alive?
    private Point2D _position; // position, no specific units
    private double _orientation; // orientation, in radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Default constructor, sets position and orientation to zero.
     */
    public DynamicObject() {
        this( new Point2D.Double( 0, 0 ), 0 );
    }
    
    /**
     * Constructor with specific position and orientation.
     * @param position position
     * @param orientation orientation, in radians
     */
    public DynamicObject( Point2D position, double orientation ) {
        super();
        _id = System.currentTimeMillis();
        _isAlive = true;
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the object's unique identifier.
     * 
     * @return String
     */
    public String getId() {
        return String.valueOf( _id );
    }
    
    /**
     * Gets the position. 
     * Since Point2D is mutable, this method allocates a new Point2D.
     * If you don't intend to modify the point, use getPositionRef,
     * which returns a reference to the point.
     * 
     * @return a copy of the Point2D that contains the position
     */
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    /**
     * Returns a reference to the Point2D that stores the position.
     * If you modify the Point2D returned, you will surely burn in hell.
     * 
     * @return a reference to the Point2D that contains the position
     */
    public Point2D getPositionRef() {
        return _position;
    }
    
    /**
     * Gets the x coordinate of the object's position.
     * @return double
     */
    public double getX() {
        return _position.getX();
    }
    
    /**
     * Gets the y coordinate of the object's position.
     * @return double
     */
    public double getY() {
        return _position.getY();
    }
    
    /**
     * Sets the position.
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {
        if ( x != _position.getX() && y != _position.getY() ) {
            _position.setLocation( x, y );
            notifyObservers( PROPERTY_POSITION );   
        }
    }
    
    /**
     * Sets the position.
     * @param position
     */
    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }
    
    /**
     * Gets the objects orientation, in degrees.
     * @return double
     */
    public double getOrientation() {
        return _orientation;
    }
    
    /**
     * Sets the objects orientation, in degrees.
     * @param direction
     */
    public void setOrientation( double direction ) {
        if ( direction != _orientation) {
            _orientation = direction;
            notifyObservers( PROPERTY_ORIENTATION );
        }
    }
    
    /**
     * Kills the object.
     */
    public void kill() {
        if ( _isAlive ) {
            _isAlive = false;
            notifyObservers( PROPERTY_ALIVE );
        }
    }
    
    /**
     * Is this object alive?
     * @return true or false
     */
    public boolean isAlive() {
        return _isAlive;
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
