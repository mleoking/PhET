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
import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * WireCoil is the model of a coil of wire loops.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WireCoil extends SimpleObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Radius of the coil
    private double _radius;
    // Gauge of wire used for all loops in the coil
    private int _gauge;
    // Location of the coil
    private Point2D _location;
    // Direction, in degrees
    private double _direction;
    // Wire loops that make up the coil (array of WireLoop)
    private ArrayList _loops;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor.
     * Creates a coil with one loop, radius=10, gauge=1, location=(0,0), direction=0.0
     */
    public WireCoil() {
        _radius = 10.0;
        _gauge = 1;
        _location = new Point2D.Double( 0, 0 );
        _direction = 0.0;
        _loops = new ArrayList();
        
        setNumberOfLoops( 1 );
    }
    
    /**
     * Partially-specified constructor.
     * Creates the specified coil with location=(0,0), direction=0.0
     * 
     * @param numberOfLoops number of wire loops in the coil
     * @param radius radius used for all wire loops
     * @param gauge gauge of wire used for all wire loops
     */
    public WireCoil( int numberOfLoops, double radius, int gauge ) {
        this( numberOfLoops, radius, gauge, new Point2D.Double(0,0), 0.0 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param numberOfLoops number of wire loops in the coil
     * @param radius radius used for all wire loops
     * @param gauge gauge of wire used for all wire loops
     * @param location location of the coil
     * @param direction direction in degrees (see setDirection)
     */
    public WireCoil( int numberOfLoops, double radius, int gauge, Point2D location, double direction ) {
        this();
        _radius = radius;
        _gauge = gauge;
        _location.setLocation( location.getX(), location.getY() );
        _direction = direction;
        setNumberOfLoops( numberOfLoops );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the number of loops in the coil.
     * This method destroys any existing loops and creates a new set.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfLoops( int numberOfLoops ) {
        _loops.clear();
        for( int i = 0; i < numberOfLoops; i++ ) {
            Point2D location = new Point2D.Double( _location.getX() + (i * _gauge), _location.getY() );
            _loops.add( new WireLoop( _radius, _gauge, location, _direction ) );
        }
    }
    
    /**
     * Gets the number of loops in the coil.
     * 
     * @return the number of loops
     */
    public int getNumberOfLoops() {
        return _loops.size();
    }
    
    /**
     * Sets the radius of the coil.
     * This radius is shared by all loops in the coil.
     * 
     * @param radius the radius
     */
    public void setRadius( double radius ) {
        _radius = radius;
        updateLoops();
        notifyObservers();
    }
    
    /**
     * Gets the radius of the coil.
     * 
     * @return the radius
     */
    public double getRadius() {
        return _radius;
    }
    
    /**
     * Gets the total surface area of all loops in the coil.
     * 
     * @return the area
     */
    public double getArea() {
        return getNumberOfLoops() * Math.PI * Math.pow( getRadius(), 2.0 );
    }
    
    /**
     * Sets the gauge of wire used for the loops in the coil.
     * Note that this does not behave like tradition wire gauge,
     * where (for example) 14 gauge is thicker than 20 gauge.
     * This is simply the width in pixels of the wire.
     * 
     * @param gauge the wire gauge
     */
    public void setGauge( int gauge ) {
        _gauge = gauge;
        updateLoops();
        notifyObservers();
    }
    
    /**
     * Gets the gauge of wire used for the loops.
     * 
     * @return the gauge
     */
    public int getGauge() {
        return _gauge;
    }
    
    /**
     * Sets the location of the coil.
     * This determines the location of the left edge of the coil, prior to 
     * applying any direction rotation.
     * 
     * @param location the location
     */
    public void setLocation( Point2D location ) {
        this.setLocation( location.getX(), location.getY() );
    }
    
    /**
     * Convenience method for setting the location.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( double x, double y ) {
        _location.setLocation( x, y );
        updateLoops();
        notifyObservers();
    }
    
    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point2D getLocation() {
        return new Point2D.Double( _location.getX(), _location.getY() );
    }
    
    /**
     * Gets the X coordinate of the location.
     * 
     * @return the X coordinate
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * Gets the Y coordinate of the location.
     * 
     * @return the Y coordinate
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * Sets the direction. 
     * A direction of zero makes the loops' surface area vectors parallel to the X axis.
     * 
     * @param direction the direction, in degrees
     */
    public void setDirection( double direction ) {
        _direction = direction;
        updateLoops();
        notifyObservers();
    }
    
    /**
     * Gets the direction.
     * 
     * @return the direction, in degrees.
     */
    public double getDirection() {
        return _direction;
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Updates the loops based on changes made to the coil.
     */
    private void updateLoops() {
        WireLoop loop = null;
        for( int i = 0; i < _loops.size(); i++ ) {
            loop = (WireLoop)_loops.get(i);
            loop.setRadius( _radius );
            loop.setGauge( _gauge );
            loop.setLocation( _location.getX() + (i * _gauge), _location.getY() );
            loop.setDirection( _direction );
        }
    }
}
