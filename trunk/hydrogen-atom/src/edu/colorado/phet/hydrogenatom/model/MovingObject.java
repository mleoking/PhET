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

/**
 * MovingObject is any object that has a position and direction of motion.
 * Both the position and direction are mutable.
 * Observers receive notification when the position or direction properties change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MovingObject extends StationaryObject {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /** Property used to notify observers of a direction change */
    public static final String PROPERTY_DIRECTION = "direction";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* Stores the object's current direction, in degrees */
    private double _direction;
    
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
}
