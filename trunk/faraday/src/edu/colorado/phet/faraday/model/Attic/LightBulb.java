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
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * LightBulb is the model of a lightbulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulb extends AbstractResistor implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MAX_VOLTAGE = 120.0; // XXX
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Current _currentModel;
    private double _intensity; // 0-1
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param currentModel the model of the current running through the bulb
     * @param resistance the resistance of the bulb
     */
    public LightBulb( Current currentModel, double resistance ) {
        super( resistance );
        _currentModel = currentModel;
        _currentModel.addObserver( this );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _currentModel.removeObserver( this );
        _currentModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    private void setIntensity( double intensity ) {
        _intensity = intensity;
        notifyObservers();
    }
    
    public double getIntensity() {
        return _intensity;
    }
   
    public double getCurrent() {
        return _currentModel.getAmps();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        double voltage = getCurrent() * getResistance();
        double intensity = MathUtil.clamp( 0, (voltage / MAX_VOLTAGE), 1 );
        setIntensity( intensity );
    }
    
    
}
