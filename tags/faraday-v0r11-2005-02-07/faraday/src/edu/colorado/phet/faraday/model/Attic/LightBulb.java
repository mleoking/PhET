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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * LightBulb is the model of a lightbulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulb extends SimpleObservable implements SimpleObserver {

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
     * @param pickupCoilModel the pickup coil that the lightbulb is across
     */
    public LightBulb( PickupCoil pickupCoilModel ) {
        super();
        
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
     * Gets the intensity of the light.
     * Fully off is 0.0, fully on is 1.0.
     * 
     * @return the intensity (0.0 - 1.0)
     */
    public double getIntensity() {
        double voltage = Math.abs( _pickupCoilModel.getVoltage() );
        double intensity = voltage / FaradayConfig.MAX_EMF;
        intensity = MathUtil.clamp( 0, intensity, 1 );
        if ( intensity == Double.NaN ) {
            System.out.println( "WARNING: LightBulb.stepInTime: intensity=NaN" );
            intensity = 0.0;
        }
        return intensity;
    }
    
    /**
     * Enables or disables the state of the lightbulb.
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
     * Gets the state of the lightbulb.  See setEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        if ( isEnabled() ) {
            notifyObservers();
        }
    }
}