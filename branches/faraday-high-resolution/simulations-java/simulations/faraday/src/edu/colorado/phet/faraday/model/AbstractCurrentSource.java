/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.model;


/**
 * AbstractCurrentSource is the abstract base class for all things that are 
 * capable of acting as a current source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCurrentSource extends FaradayObservable {

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
    public AbstractCurrentSource() {
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
    
    /*
     * NOTE! 
     * There is intentionally no setVoltage method; do NOT add one.
     * Voltage must be controlled via setAmplitude.
     */
    
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
    
    /**
     * Sets the voltage amplitude.
     * This indicates how the voltage relates to the maximum voltage.
     * 
     * @param amplitude -1...+1
     */
    public void setAmplitude( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= 1 );
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the voltage amplitude.
     * This indicates how the voltage relates to the maximum voltage.
     * 
     * @return the amplitude, -1...+1
     */
    public double getAmplitude() {
        return _amplitude;  
    }
}
