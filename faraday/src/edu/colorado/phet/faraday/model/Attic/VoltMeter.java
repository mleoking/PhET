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

import edu.colorado.phet.common.math.MedianFilter;


/**
 * VoltMeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeter extends AbstractResistor {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int HISTORY_SIZE = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _voltage;
    private double[] _voltageHistory;
    private boolean _smoothingEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param resistance the resistance of the meter
     */
    public VoltMeter( double resistance ) {
        super( resistance );
        _voltage = 0.0;
        _smoothingEnabled = false;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the voltage that the meter is reading.
     * 
     * @param voltage the voltage, in volts
     */
    private void setVoltage( double voltage ) {
        if ( voltage != _voltage ) {
            //System.out.println( "VoltMeter.setVoltage: voltage=" + voltage ); // DEBUG
            _voltage = voltage;
            notifyObservers();
        }
    }
    
    /**
     * Gets the voltage that the meter is reading.
     * 
     * @return the voltage, in volts
     */
    public double getVoltage() {
        return _voltage;
    }
    
    /**
     * Smooths out the behavior of the voltmeter by removing spikes in the data.
     * Changing the value of this property has the side-effect of clearing the 
     * data history.
     * 
     * @param smoothingEnabled true to enable, false to disable
     */
    public void setSmoothingEnabled( boolean smoothingEnabled ) {
        if ( smoothingEnabled != _smoothingEnabled ) {
            _smoothingEnabled = smoothingEnabled;
            for ( int i = 0; i < HISTORY_SIZE; i++ ) {
                _voltageHistory[i] = 0.0;
            }
        }
    }
    
    /**
     * Gets the smoothing state. See setSmoothingEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isSmoothingEnabled() {
        return _smoothingEnabled;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        double voltage = getCurrent() * getResistance();
        if ( _smoothingEnabled ) {
            // Take a median to remove spikes in data.
            for ( int i = HISTORY_SIZE - 1; i > 0; i-- ) {
                _voltageHistory[i] = _voltageHistory[i - 1];
            }
            _voltageHistory[0] = voltage;
            setVoltage( MedianFilter.getMedian( _voltageHistory ) );
        }
        else {
            setVoltage( voltage );
        }
    }
}
