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
 * Lightbulb is the model of a lightbulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Lightbulb extends FaradayObservable implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private double _scale;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param pickupCoilModel the pickup coil that the lightbulb is across
     */
    public Lightbulb( PickupCoil pickupCoilModel ) {
        super();
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );

        _scale = 1.0;
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
        double intensity = _scale * Math.abs( _pickupCoilModel.getEmf() ) / FaradayConfig.MAX_PICKUP_EMF;
        intensity = MathUtil.clamp( 0, intensity, 1 );
        
        // Intensity below the threshold is effectively zero.
        if ( intensity < FaradayConfig.LIGHTBULB_INTENSITY_THRESHOLD ) {
            intensity = 0;
        }
        
        return intensity;
    }
    
    /**
     * Sets the scale that is applied to the intensity.
     * 
     * @param scale
     */
    public void setScale( double scale ) {
        assert( scale > 0 );
        if ( scale != _scale ) {
            _scale = scale;
            notifyObservers();
        }
    }

    /**
     * Gets the scale that is applied to the intensity.
     * 
     * @return the scale
     */
    public double getScale() {
        return _scale;
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