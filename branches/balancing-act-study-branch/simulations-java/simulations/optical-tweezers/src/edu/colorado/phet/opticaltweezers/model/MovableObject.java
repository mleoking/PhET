// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;


/**
 * MovableObject is an object that has mutable position, orientation and speed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MovableObject extends FixedObject {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_ORIENTATION = "orientation";
    public static final String PROPERTY_SPEED = "speed";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _speed; // distance moved per dt
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Default constructor, sets position and orientation to zero.
     */
    public MovableObject() {
        this( new Point2D.Double( 0, 0 ), 0, 0 );
    }
    
    /**
     * Constructor with specific position and orientation.
     * @param position position
     * @param orientation orientation, in radians
     * @param speed
     */
    public MovableObject( Point2D position, double orientation, double speed ) {
        super( position, orientation );
        _speed = speed;
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the position.
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {
        if ( x != _position.getX() || y != _position.getY() ) {
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
     * Sets the orientation, in radians.
     * @param orientation
     */
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation) {
            _orientation = orientation;
            notifyObservers( PROPERTY_ORIENTATION );
        }
    }
    
    /**
     * Sets the speed.
     * @param speed
     */
    public void setSpeed( double speed ) {
        if ( speed != _speed ) {
            _speed = speed;
            notifyObservers( PROPERTY_SPEED );
        }
    }
    
    /**
     * Gets the speed.
     * @return
     */
    public double getSpeed() {
        return _speed;
    }
}
