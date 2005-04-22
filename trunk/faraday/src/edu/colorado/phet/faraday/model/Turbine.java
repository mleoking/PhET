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
public class Turbine extends DipoleMagnet implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Determines whether all critical angles will be hit.
    private static boolean _criticalAnglesEnabled = false;
    
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
        double framesPerSecond = FaradayConfig.CLOCK_FRAME_RATE;
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
    
    /**
     * Enables or disables critical angles.
     * When enabled, the delta angle may be adjusted so that the rotation
     * hits the points where it is parallel and perpendicular to the pickup coil.
     * 
     * @param criticalAnglesEnabled true or false
     */
    public static void setCriticalAnglesEnabled( boolean criticalAnglesEnabled ) {
        _criticalAnglesEnabled = criticalAnglesEnabled;
    }
    
    /**
     * Determines whether critical angles are enabled.
     * See etCriticalAnglesEnabled.
     * 
     * @return true or false
     */
    public static boolean isCriticalAnglesEnabled() {
        return _criticalAnglesEnabled;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        
        if ( _speed != 0 ) {
            
            double previousDirection = getDirection();
            double newDirection = getDirection() + ( dt * _speed * _maxDelta );

            // Adjust the angle so that we hit all peaks and zero crossings.
            if ( _criticalAnglesEnabled ) {
                for ( int i = 0; i < 5; i++ ) {
                    double criticalAngle = i * ( Math.PI / 2 ); // ...at 90 degree intervals
                    if ( _speed > 0 && newDirection > criticalAngle && previousDirection < criticalAngle ) {
                        newDirection = criticalAngle;
                        break;
                    }
                    else if ( _speed < 0 && newDirection < -criticalAngle && previousDirection > -criticalAngle ) {
                        newDirection = -criticalAngle;
                        break;
                    }
                }
            }
            
            // Limit direction to -360...+360 degrees.
            int sign = ( newDirection < 0 ) ? -1 : +1;
            newDirection = sign * ( Math.abs( newDirection )  % ( 2 * Math.PI ) );

            setDirection( newDirection );
        }
    }
}
