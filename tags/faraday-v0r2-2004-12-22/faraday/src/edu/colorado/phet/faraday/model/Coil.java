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
 * Coil is the model of a coil of wire loops.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Coil extends SimpleObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Radius of the coil
    private double _radius;
    // Location of the coil
    private Point2D _location;
    // Direction, in degrees
    private double _direction;
    // Spacing between the loops
    private double _spacing;
    // Wire loops that make up the coil (array of Loop)
    private ArrayList _loops;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor.
     * Creates a default coil with one loop, radius=10.0, location=(0,0), direction=0.0, spacing=1.0
     */
    public Coil() {
        this( 1, 10.0 );
    }
    
    /**
     * Partially-specified constructor.
     * Creates the specified coil with location=(0,0), direction=0.0, spacing=1.0
     * 
     * @param numberOfLoops number of wire loops in the coil
     * @param radius radius used for all wire loops
     */
    public Coil( int numberOfLoops, double radius ) {
        this( numberOfLoops, radius, new Point2D.Double(0,0), 0.0, 1.0 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param numberOfLoops number of wire loops in the coil
     * @param radius radius used for all wire loops
     * @param location location of the coil
     * @param direction direction in degrees (see setDirection)
     * @param spacing spacing between the loops
     */
    public Coil( int numberOfLoops, double radius, Point2D location, double direction, double spacing ) {
        _radius = radius;
        _location = new Point2D.Double( location.getX(), location.getY() );
        _direction = direction;
        _spacing = spacing;
        _loops = new ArrayList();
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
            Point2D location = new Point2D.Double( _location.getX() + (i * _spacing), _location.getY() );
            _loops.add( new Loop( _radius, location, _direction ) );
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
     * Sets the spacing between loops in the coil.
     * 
     * @param spacing the spacing
     */
    public void setSpacing( double spacing ) {
        _spacing = spacing;
        updateLoops();
        notifyObservers();
    }
    
    /**
     * Gets the spacing between the loops.
     * 
     * @return the spacing
     */
    public double getSpacing() {
        return _spacing;
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
        Loop loop = null;
        for( int i = 0; i < _loops.size(); i++ ) {
            loop = (Loop)_loops.get(i);
            loop.setRadius( _radius );
            loop.setLocation( _location.getX() + (i * _spacing), _location.getY() );
            loop.setDirection( _direction );
        }
    }
}
