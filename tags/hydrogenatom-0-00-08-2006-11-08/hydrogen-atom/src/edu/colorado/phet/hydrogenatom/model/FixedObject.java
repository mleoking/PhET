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
 * FixedObject is an object that has a fixed (immutable) position and orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FixedObject extends HAObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private long _id; // unique identifier
    protected Point2D _position; // position, no specific units
    protected double _orientation; // orientation, in radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Default constructor, sets position and orientation to zero.
     */
    public FixedObject() {
        this( new Point2D.Double( 0, 0 ), 0 );
    }
    
    /**
     * Constructor with specific position and orientation.
     * @param position position
     * @param orientation orientation, in radians
     */
    public FixedObject( Point2D position, double orientation ) {
        super();
        _id = System.currentTimeMillis();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
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
     * Gets the orientation, in degrees.
     * @return double
     */
    public double getOrientation() {
        return _orientation;
    }
}
