/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConstants;


/**
 * Lightbulb is the model of a lightbulb.
 * Its intensity is a function of the current in the pickup coil.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Lightbulb extends FaradayObservable implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private double _scale;
    private double _previousCurrentAmplitude;
    private boolean _offWhenCurrentChangesDirection;
    
    //----------------------------------------------------------------------------
    // Constructors
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
        _previousCurrentAmplitude = 0.0;
        _offWhenCurrentChangesDirection = false;
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
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
        
        double intensity = 0.0;
        
        final double currentAmplitude = _pickupCoilModel.getCurrentAmplitude();
        
        if ( _offWhenCurrentChangesDirection && 
            ( ( currentAmplitude > 0 && _previousCurrentAmplitude <= 0 ) || 
              ( currentAmplitude <= 0 && _previousCurrentAmplitude > 0 ) ) ) {
             // Current changed direction, so turn the light off.
             intensity = 0.0;
        }
        else {
            // Light intensity is proportional to amplitude of current in the coil.
            intensity = _scale * Math.abs( currentAmplitude );
            intensity = MathUtil.clamp( 0, intensity, 1 );
            
            // Intensity below the threshold is effectively zero.
            if ( intensity < FaradayConstants.CURRENT_AMPLITUDE_THRESHOLD ) {
                intensity = 0;
            }
        }
        
        _previousCurrentAmplitude = currentAmplitude;
        
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
    
    /**
     * Determines whether the lightbulb turns off when the current in the coil
     * changes direction.  In some cases (eg, the Generator or AC Electromagnet)
     * this is the desired behavoir.  In other cases (eg, polarity file of the 
     * Bar Magnet) this is not the desired behavior.
     * 
     * @param offWhenCurrentChangesDirection true or false
     */
    public void setOffWhenCurrentChangesDirection( boolean offWhenCurrentChangesDirection ) {
        _offWhenCurrentChangesDirection = offWhenCurrentChangesDirection;
    }
    
    /**
     * Determines whether the lightbulb turns off when the current in the coil
     * changes direction.
     * 
     * @return true or false
     */
    public boolean isOffWhenCurrentChangesDirection() {
        return _offWhenCurrentChangesDirection;
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