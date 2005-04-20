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



/**
 * AbstractVoltageSource is the base class for all things that are 
 * capable of creating voltage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractVoltageSource extends FaradayObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_MAX_VOLTAGE = Double.POSITIVE_INFINITY;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _maxVoltage;
    private double _amplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public AbstractVoltageSource() {
        _maxVoltage = DEFAULT_MAX_VOLTAGE;
        _amplitude = 1.0; // full strength
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the voltage.
     * 
     * @return the voltage, in volts
     */
    public double getVoltage() {
        return _amplitude * _maxVoltage;
    }
    
    /**
     * Sets the maximum voltage that this voltage source will produce.
     * 
     * @param maxVoltage the maximum voltage, in volts
     */
    public void setMaxVoltage( double maxVoltage ) {
        _maxVoltage = maxVoltage;
        notifySelf();
        notifyObservers();
    }
    
    /**
     * Gets the maximum voltage that this voltage source will produce.
     * 
     * @return the maximum voltage, in volts
     */
    public double getMaxVoltage() {
        return _maxVoltage;
    }
    
    
    public void setAmplitude( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= 1 );
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            notifySelf();
            notifyObservers();
        }
    }
    
    public double getAmplitude() {
        return _amplitude;  
    }
}
