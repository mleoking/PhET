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
import edu.colorado.phet.common.math.MedianFilter;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * LightBulb is the model of a lightbulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulb extends AbstractResistor {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private static final int HISTORY_SIZE = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _intensity; // 0-1
    private double[] _intensityHistory;
    private boolean _smoothingEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param resistance the resistance of the bulb
     */
    public LightBulb( double resistance ) {
        super( resistance );
        _intensity = 0.0;
        _intensityHistory = new double[ HISTORY_SIZE ];
        _smoothingEnabled = false;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the intensity of the light.
     * Fully off is 0.0, fully on is 1.0.
     * 
     * @param intensity (0.0 - 1.0)
     * @throws IllegalArgumentException if intensity is out of range
     */
    private void setIntensity( double intensity ) {
        if ( ! (intensity >= 0 && intensity <= 1 ) ) {
            throw new IllegalArgumentException( "intensity must be >= 0 and <= 1: " + intensity );
        }
        if ( intensity != _intensity ) {
            //System.out.println( "LightBulb.setIntensity: intensity=" + intensity ); // DEBUG
            _intensity = intensity;
            notifyObservers();
        }
    }
    
    /**
     * Gets the intensity of the light.
     * Fully off is 0.0, fully on is 1.0.
     * 
     * @return the intensity (0.0 - 1.0)
     */
    public double getIntensity() {
        return _intensity;
    }

    /**
     * Smooths out the behavior of the light intensity by removing spikes in the data.
     * Changing the value of this property has the side-effect of clearing the 
     * data history.
     * 
     * @param smoothingEnabled true to enable, false to disable
     */
    public void setSmoothingEnabled( boolean smoothingEnabled ) {
        if ( smoothingEnabled != _smoothingEnabled ) {
            _smoothingEnabled = smoothingEnabled;
            for ( int i = 0; i < HISTORY_SIZE; i++ ) {
                _intensityHistory[i] = 0.0;
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
        double intensity = MathUtil.clamp( 0, Math.abs( voltage / FaradayConfig.MAX_EMF ), 1 );
        if ( intensity == Double.NaN ) {
            System.out.println( "WARNING: LightBulb.stepInTime - intensity=NaN" );
        }
        else {
            if ( _smoothingEnabled ) {
                // Take a median to remove spikes in data.
                for ( int i = HISTORY_SIZE - 1; i > 0; i-- ) {
                    _intensityHistory[i] = _intensityHistory[i - 1];
                }
                _intensityHistory[0] = intensity;
                setIntensity( MedianFilter.getMedian( _intensityHistory ) );
            }
            else {
                setIntensity( intensity );
            }
        }
    }
}
