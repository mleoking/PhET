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
 * Voltmeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Voltmeter extends SimpleObservable implements SimpleObserver {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int HISTORY_SIZE = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param pickupCoilModel the pickup coil that the meter is across
     */
    public Voltmeter( PickupCoil pickupCoilModel ) {
        super();
        
        assert( pickupCoilModel != null );
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        _enabled = true;
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _pickupCoilModel.removeObserver( this );
        _pickupCoilModel = null;
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
        return _pickupCoilModel.getVoltage();
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
