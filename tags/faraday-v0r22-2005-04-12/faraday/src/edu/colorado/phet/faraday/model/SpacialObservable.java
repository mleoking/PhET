/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * SpacialObservable is an observable object that has a location and direction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpacialObservable extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Point2D _location;
    private double _direction;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor.
     */
    public SpacialObservable() {
        super();
        _location = new Point2D.Double( 0, 0 );
        _direction = 0.0;
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param location the location
     * @param direction the direction, in degrees
     */
    public SpacialObservable( Point2D location, double direction ) {
        super();
        assert( location != null );
        _location = new Point2D.Double( location.getX(), location.getY() );
        _direction = direction;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the location in 2D space.
     * 
     * @param location the location
     */
    public void setLocation( final Point2D location ) {
        assert( location != null );
        setLocation( location.getX(), location.getY() );
    }
    
    /**
     * Sets the location in 2D space.
     * 
     * @param x location X coordinate
     * @param y location Y coordinate
     */
    public void setLocation( double x, double y ) {
        if ( x != _location.getX() || y != _location.getY() ) {
            _location.setLocation( x, y );
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the location in 2D space.
     * 
     * @return the location
     */
    public Point2D getLocation() {
        return getLocation( null );
    }
    
    /**
     * Gets the location in 2D space.
     * 
     * @param outputPoint the point into which the location should be written, possibly null
     * @return the location
     */
    public Point2D getLocation( Point2D outputPoint /* output */ ) {
        if ( outputPoint == null ) {
            outputPoint = new Point2D.Double( _location.getX(), _location.getY() );
        }
        else {
            outputPoint.setLocation( _location.getX(), _location.getY() );
        }
        return outputPoint;
    }
    
    /**
     * Gets the location's X coordinate.
     * 
     * @return X coordinate
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * Gets the location's Y coordinate.
     * 
     * @return Y coordinate
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * Sets the direction.
     * Positive values indicate clockwise rotation.
     * 
     * @param direction the direction, in radians
     */
    public void setDirection( double direction ) {
        if ( direction != _direction ) {
            _direction = direction;
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the direction.
     * 
     * @return the direction, in radians.
     */
    public double getDirection() {
        return _direction;
    }
    
    /**
     * Hook for any updates that subclasses might need to perform.
     */
    protected void updateSelf() {}
}
