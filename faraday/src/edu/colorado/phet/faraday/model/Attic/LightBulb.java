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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * LightBulb is the model of a lightbulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulb extends AbstractResistor implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MAX_VOLTAGE = 120.0; // XXX
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IVoltageSource _voltageSourceModel;
    private double _intensity; // 0-1
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param voltageSourceModel the model of the voltage across the bulb
     * @param resistance the resistance of the bulb
     */
    public LightBulb( IVoltageSource voltageSourceModel, double resistance ) {
        super( resistance );
        _voltageSourceModel = voltageSourceModel;
        _intensity = 0.0;
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
        if ( intensity < 0 || intensity > 1 ) {
            throw new IllegalArgumentException( "intensity must be >= 0 and <= 1: " + intensity );
        }
        if ( intensity != _intensity ) {
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

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        // XXX average intensity over time!
        double voltage = _voltageSourceModel.getVoltage();
        double intensity = MathUtil.clamp( 0, (voltage / MAX_VOLTAGE), 1 );
        setIntensity( intensity );
    }
    
    
}
