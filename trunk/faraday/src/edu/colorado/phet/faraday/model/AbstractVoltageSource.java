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

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractVoltageSource is the base class for all things that are 
 * capable of creating voltage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractVoltageSource extends SpacialObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_MAX_VOLTAGE = 100.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _voltage;
    private double _maxVoltage;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public AbstractVoltageSource() {
        _voltage = 0.0;
        _enabled = true;
        _maxVoltage = DEFAULT_MAX_VOLTAGE;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the voltage.
     * 
     * @param voltage the voltage, in volts
     */
    public void setVoltage( double voltage ) {
        if ( voltage != _voltage ) {
            _voltage = voltage;
            if ( _enabled ) {
                updateSelf();
                notifyObservers();
            }
        }
    }
    
    /**
     * Gets the voltage.
     * 
     * @return the voltage, in volts
     */
    public double getVoltage() {
        return _voltage;
    }
    
    /**
     * Sets the maximum voltage that this voltage source will produce.
     * 
     * @param maxVoltage the maximum voltage, in volts
     */
    public void setMaxVoltage( double maxVoltage ) {
        _maxVoltage = maxVoltage;
        updateSelf();
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
     * Enabled/disables this object.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Is this object enabled?
     * 
     * @return true or false
     */
    public boolean isEnabled() {
        return _enabled;
    }
}
