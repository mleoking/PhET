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
    private double _maxRotationDelta; // in radians
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public Turbine() {
        super();
        _speed = 0;
        _maxRotationDelta = DEFAULT_MAX_ROTATION_DELTA;
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
     * Sets the rotation delta that will occur
     * when the turbine is running at full speed.
     * 
     * @param maxDelta the maximum rotation delta, in radians
     */
    public void setMaxRotationDelta( double maxDelta ) {
        _maxRotationDelta = maxDelta;
    }
    
    /**
     * Gets the rotation delta that will occur
     * when the turbine is running at full speed.
     * 
     * @return the maximum rotation delta, in radians
     */
    public double getMaxRotationDelta() {
        return _maxRotationDelta;
    }
    
    /**
     * Gets the number of rotations per minute.
     * 
     * @return rotations per minute
     */
    public double getRPM() {
        double framesPerSecond = 24; //XXX
        double framePerMinute = 60 * framesPerSecond;
        double maxRPM = ( framePerMinute * _maxRotationDelta ) / ( 2 * Math.PI );
        return Math.abs( _speed * maxRPM );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        double delta = _speed * _maxRotationDelta;
        setDirection( getDirection() + delta );
    }
}
