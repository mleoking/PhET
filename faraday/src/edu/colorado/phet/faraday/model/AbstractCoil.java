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

import edu.colorado.phet.faraday.FaradayConfig;


/**
 * AbstractCoil is the abstract base class for all coils.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractCoil extends AbstractVoltageSource {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Number of loops in the coil.
    private int _numberOfLoops;
    // Radius of all loops in the coil.
    private double _radius;
    // Width of the wire.
    private double _wireWidth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor.
     * Creates a default coil with one loop, radius=10.0, wireWidth=16.0
     */
    public AbstractCoil() {
        this( 1, 10.0, 16.0 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param numberOfLoops number of loops in the coil
     * @param radius radius used for all loops
     */
    public AbstractCoil( int numberOfLoops, double radius, double wireWidth ) {
        setNumberOfLoops( numberOfLoops );
        setRadius( radius );
        setWireWidth( wireWidth );
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
     * Sets the width of the wire used for the coil.
     * 
     * @param wireWidth the wire width, in pixels
     */
    public void setWireWidth( double wireWidth ) {
        if ( ! (wireWidth > 0) ) {
            throw new IllegalArgumentException( "wireWidth must be > 0: " + wireWidth );
        }
        if ( wireWidth != _wireWidth ) {
            _wireWidth = wireWidth;
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the width of the wire used for the coil.
     * 
     * @return the wire width, in pixels
     */
    public double getWireWidth() {
        return _wireWidth;
    }
}
