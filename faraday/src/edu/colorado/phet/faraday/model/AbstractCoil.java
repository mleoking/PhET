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
    // Spacing between the loops
    private double _loopSpacing;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor.
     * Creates a default coil with one loop, radius=10, wireWidth=16, loopSpacing=25
     */
    public AbstractCoil() {
        this( 1, 10, 16, 25 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param numberOfLoops number of loops in the coil
     * @param radius radius used for all loops
     */
    public AbstractCoil( int numberOfLoops, double radius, double wireWidth, double loopSpacing ) {
        setNumberOfLoops( numberOfLoops );
        setRadius( radius );
        setWireWidth( wireWidth );
        setLoopSpacing( loopSpacing );
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
        assert( numberOfLoops >  0 );
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
     */
    public void setRadius( double radius ) {
        assert( radius > 0 );
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
     * Sets the surface area of one loop.
     * 
     * @param area the area
     */
    public void setLoopArea( double area ) {
        double radius = Math.sqrt( area / Math.PI );
        setRadius( radius );
    }
    
    /**
     * Gets the surface area of one loop.
     * 
     * @return the area
     */
    public double getLoopArea() {
        return ( Math.PI * _radius * _radius );
    }
    
    /**
     * Sets the width of the wire used for the coil.
     * 
     * @param wireWidth the wire width, in pixels
     */
    public void setWireWidth( double wireWidth ) {
        assert( wireWidth > 0 );
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
    
    /**
     * Sets the spacing between loops in the coil.
     * 
     * @param loopSpacing the spacing, in pixels
     */
    public void setLoopSpacing( double loopSpacing ) {
        assert( loopSpacing > 0 );
        if ( loopSpacing != _loopSpacing ) {
            _loopSpacing = loopSpacing;
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the spacing between loops in the coil.
     * 
     * @return the spacing, in pixels
     */
    public double getLoopSpacing() {
        return _loopSpacing;
    }
}
