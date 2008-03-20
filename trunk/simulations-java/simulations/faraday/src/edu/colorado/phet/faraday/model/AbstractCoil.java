/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

/**
 * AbstractCoil is the abstract base class for all coils.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCoil extends FaradayObservable {
    
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
    // Amplitude of the current in the coil (-1...+1)
    private double _currentAmplitude;

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
     * @param wireWidth width of the wire
     * @param loopSpacing space between the loops
     */
    public AbstractCoil( int numberOfLoops, double radius, double wireWidth, double loopSpacing ) {
        _numberOfLoops = numberOfLoops;
        _radius = radius;
        _wireWidth = wireWidth;
        _loopSpacing = loopSpacing;
        _currentAmplitude = 0.0;
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
            notifySelf();
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
            notifySelf();
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
            notifySelf();
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
            notifySelf();
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
    
    /**
     * Sets the current amplitude in the coil.
     *
     * @param currentAmplitude the current amplitude (-1...+1)
     */
    public void setCurrentAmplitude( double currentAmplitude ) {
        assert( currentAmplitude >= -1 && currentAmplitude <= +1 );
        if ( currentAmplitude != _currentAmplitude ) {
            _currentAmplitude = currentAmplitude;
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the current amplitude in the coil.
     * 
     * @return the current amplitude
     */
    public double getCurrentAmplitude() {
        return _currentAmplitude;
    }
}
