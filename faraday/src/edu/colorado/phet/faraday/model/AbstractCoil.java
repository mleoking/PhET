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


/**
 * AbstractCoil is the abstract base class for all coils.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractCoil extends SpacialObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Number of loops in the coil.
    private int _numberOfLoops;
    // Radius of all loops in the coil.
    private double _radius;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor.
     * Creates a default coil with one loop, radius=10.0, location=(0,0), direction=0.0
     */
    public AbstractCoil() {
        this( 1, 10.0 );
    }
    
    /**
     * Partially-specified constructor.
     * Creates the specified coil with location=(0,0), direction=0.0
     * 
     * @param numberOfLoops number of loops in the coil
     * @param radius radius used for all loops
     */
    public AbstractCoil( int numberOfLoops, double radius ) {
        this( numberOfLoops, radius, new Point2D.Double(0,0), 0.0 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param numberOfLoops number of loops in the coil
     * @param radius radius used for all loops
     * @param location location of the coil
     * @param direction direction in degrees (see setDirection)
     */
    public AbstractCoil( int numberOfLoops, double radius, Point2D location, double direction ) {
        super( location, direction );
        assert( location != null );
        setNumberOfLoops( numberOfLoops );
        setRadius( radius );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the number of loops in the coil.
     * This method destroys any existing loops and creates a new set.
     * 
     * @param numberOfLoops the number of loops
     * @throws IllegalArgumentException if numberOfLoops is not > 0
     */
    public void setNumberOfLoops( int numberOfLoops ) {
        if ( ! (numberOfLoops > 0 ) ) {
            throw new IllegalArgumentException( "numberOfLoops must be > 0: " + numberOfLoops );
        }
        if ( numberOfLoops != _numberOfLoops ) {
            _numberOfLoops = numberOfLoops;
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the number of loops in the coil.
     * 
     * @return the number of loops
     */
    public int getNumberOfLoops() {
        return _numberOfLoops;
    }
    
    /**
     * Sets the radius of the coil.
     * This radius is shared by all loops in the coil.
     * 
     * @param radius the radius
     * @throws IllegalArgumentException if radius is not > 0
     */
    public void setRadius( double radius ) {
        if ( ! (radius > 0) ) {
            throw new IllegalArgumentException( "radius must be > 0: " + radius );
        }
        if ( radius != _radius ) {
            _radius = radius;
            updateSelf();
            notifyObservers();
        }
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
     * This is a convenience function.
     * 
     * @return the area
     */
    public double getArea() {
        return _numberOfLoops * ( Math.PI * _radius * _radius );
    }
    
    /**
     * Gets the voltage across the ends of the coil.
     * 
     * @return voltage across the ends of the coil, in volts
     */
    public abstract double getVoltage();
    
    /**
     * Gets the magnet associated with the coil, if there is one.
     * 
     * @return the magnet, possibly null
     */
    public abstract AbstractMagnet getMagnet();
}
