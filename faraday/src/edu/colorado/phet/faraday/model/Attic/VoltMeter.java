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

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * VoltMeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeter extends SimpleObservable implements SimpleObserver {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int HISTORY_SIZE = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _coilModel;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param voltageSourceModel the voltage source
     */
    public VoltMeter( PickupCoil coilModel ) {
        super();
        
        assert( coilModel != null );
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        _enabled = true;
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the voltage that the meter is reading.
     * 
     * @return the voltage, in volts
     */
    public double getVoltage() {
        return _coilModel.getVoltage();
    }
    
    /**
     * Enables or disables the state of the voltmeter.
     * 
     * @param enabled true to enable, false to disable.
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers();
        }
    }
    
    /**
     * Gets the state of the voltmeter.  See setEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isEnabled() ) {
            notifyObservers();
        }
    }
}
