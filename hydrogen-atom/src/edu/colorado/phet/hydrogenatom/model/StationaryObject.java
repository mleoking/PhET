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
 * Positionable is anything that has a mutable position.
 * Observers receive notification when the position property is changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StationaryObject extends HAObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /** Property used to notify observers of a position change */
    public static final String PROPERTY_POSITION = "position";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* Stores the object's current position */
    private Point2D _position;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs an object with a default position of (0,0).
     */
    public StationaryObject() {
        this( new Point2D.Double( 0, 0 ) );
    }
    
    /**
     * Constructs an object with a specified position.
     * @param position
     */
    public StationaryObject( Point2D position ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
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
}
