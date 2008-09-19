/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;


/**
 * FaradayObservable extends SimpleObservable to by adding attributes
 * that are common to most observables in the Faraday simulation.  
 * Those attributes include:
 * <ul>
 * <li>whether the object is enabled
 * <li>location
 * <li>direction
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayObservable extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Point2D _location;
    private double _direction;
    private boolean  _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor.
     * The object is enabled, location is (0,0), direction is 0.
     */
    public FaradayObservable() {
        this( new Point2D.Double( 0, 0 ), 0.0 );
    }
    
    /**
     * Fully-specified constructor.
     * The object is enabled, located and orientated as specified.
     * 
     * @param location the location
     * @param direction the direction, in degrees
     */
    public FaradayObservable( Point2D location, double direction ) {
        super();
        assert( location != null );
        _enabled = true;
        _location = new Point2D.Double( location.getX(), location.getY() );
        _direction = direction;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Enabled/disables this object.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Is this object enabled?
     * 
     * @return true or false
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
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
            notifySelf();
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
            notifySelf();
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
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    /**
     * Hook for any updates that subclasses might need to perform.
     * Subclasses should override this to handle changes to any of 
     * the attributes defined by this base class.
     */
    protected void notifySelf() {}
}
