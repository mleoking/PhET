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


/**
 * VoltMeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeter extends AbstractResistor implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ICurrentSource _currentModel;
    private double _voltage;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param currentModel the model of the current running through the meter
     * @param resistance the resistance of the meter
     */
    public VoltMeter( ICurrentSource currentModel, double ohms ) {
        super( ohms );
        _currentModel = currentModel;
        _voltage = 0.0;
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
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        // XXX need to average voltage over time!
        setVoltage( _currentModel.getCurrent() * getResistance() );
    }
}
