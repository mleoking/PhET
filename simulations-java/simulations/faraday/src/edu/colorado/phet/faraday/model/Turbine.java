/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.faraday.FaradayConstants;


/**
 * Turbine is the model of a simple turbine. It rotates at some speed, and its 
 * rotation is measured in RPMs (rotations per minute).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Turbine extends DipoleMagnet implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _speed; // -1...+1 (see setSpeed)
    private double _maxRPM; // rotations per minute at full speed
    private double _maxDelta; // change in angle at full speed, in radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public Turbine() {
        super();
        _speed = 0.0;
        setMaxRPM( 100.0 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the speed.
     * Speed is a value between -1.0 and +1.0 inclusive.
     * The sign of the value indicates direction.
     * Zero is stopped, 1 is full speed.
     * 
     * @param speed the speed
     */
    public void setSpeed( double speed ) {
        assert( speed >= -1 && speed <= 1 );
        _speed = speed;
        notifyObservers();
    }
    
    /**
     * Gets the speed. See setSpeed.
     *
     * @return the speed
     */
    public double getSpeed() {
        return _speed;
    }
    
    /**
     * Sets the maximum rotations per minute.
     * 
     * @param maxRPM
     */
    public void setMaxRPM( double maxRPM ) {
        _maxRPM = maxRPM;
        
        // Pre-compute the maximum change in angle per clock tick.
        double framesPerSecond = FaradayConstants.CLOCK_FRAME_RATE;
        double framesPerMinute = 60 * framesPerSecond;
        _maxDelta = ( 2 * Math.PI ) * ( maxRPM / framesPerMinute );
    }
    
    /**
     * Gets the maximum rotations per minute.
     * 
     * @return the maximum rotations per minute
     */
    public double getMaxRPM() {
        return _maxRPM;
    }
    
    /**
     * Gets the number of rotations per minute at the current speed.
     * 
     * @return rotations per minute
     */
    public double getRPM() {
        return Math.abs( _speed * _maxRPM );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * Update the turbine's direction, based on its speed.
     */
    public void stepInTime( double dt ) {
        
        if ( _speed != 0 ) {
            
            // Determine the new direction
            double delta = dt * _speed * _maxDelta;
            double newDirection = getDirection() + delta;
            
            // Limit direction to -360...+360 degrees.
            int sign = ( newDirection < 0 ) ? -1 : +1;
            newDirection = sign * ( Math.abs( newDirection )  % ( 2 * Math.PI ) );

            setDirection( newDirection );
        }
    }
}
