/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * Turbine is the model of a simple turbine.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Turbine extends BarMagnet implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Rotation delta when the turbine is running at full speed.
    private static final double DEFAULT_MAX_ROTATION_DELTA = Math.PI / 3;  // radians
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _speed; // -1...+1 (see setSpeed)
    private double _maxRPM; // rotations per minute at full speed
    private double _maxDelta; // radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public Turbine() {
        super();
        _speed = 0;
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
     * Gets the speed.
     *
     * @return the speed
     * @see setSpeed
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
        double framesPerSecond = FaradayConfig.FRAME_RATE;
        double framesPerMinute = 60 * framesPerSecond;
        _maxDelta = ( 2 * Math.PI) * ( maxRPM / framesPerMinute );
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
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        double delta = _speed * _maxDelta;
        setDirection( getDirection() + delta );
    }
}
