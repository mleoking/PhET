/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.util;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/** 
 * VoltmeterRescaler is used to rescale the voltage in the voltmeter.
 * The reference is the magnetic field of a magnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltmeterRescaler extends GenericRescaler implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double THRESHOLD = 0.8;
    private static final double MIN_EXPONENT = 0.3;
    private static final double MAX_EXPONENT = 0.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param magnetModel the magnet whose magnetic field serves as the reference
     */
    public VoltmeterRescaler( AbstractMagnet magnetModel ) {
        super();
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        setMaxReference( magnetModel.getMaxStrength() );
        setReference( magnetModel.getStrength() );
        setExponents( MIN_EXPONENT, MAX_EXPONENT );
        setThreshold( THRESHOLD );
    }
    
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        setMaxReference( _magnetModel.getMaxStrength() );
        setReference( _magnetModel.getStrength() );
    }
}
