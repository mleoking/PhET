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
 * MagneticFieldRescaler makes values more visually useful by adjusting
 * them to be a bit more linear with respect to some magnetic field.
 * Since a magnetic field drops off at the rate of the distance cubed,
 * the visual effect is not very useful.  Using the magnet strength as a 
 * reference, we can rescale the field strength.
 * <p>
 * Some places where this is used include:
 * <ul>
 * <li>display of field strength by compass grid needles
 * <li>lightbulb's rays
 * <li>voltmeter reading
 * <li>electron speed in the pickup coil
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagneticFieldRescaler extends GenericRescaler  implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /*
     * WARNING! These constants control rescaling throughout the simulation.
     */
    
    // Values below this value are rescaled.
    private static final double DEFAULT_THRESHOLD = 0.8;
    
    // Approach this rescaling exponent as value approaches 1.
    private static final double DEFAULT_MAX_EXPONENT = 0.8;
    
    // Approach this rescaling exponent as value approaches 0.
    private static final double DEFAULT_MIN_EXPONENT = 0.3;
    
    private AbstractMagnet _magnetModel;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param magnetModel the magnet whose magnetic field serves as the reference
     */
    public MagneticFieldRescaler( AbstractMagnet magnetModel ) {
        super();
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        setReferenceRange( magnetModel.getMinStrength(), magnetModel.getMaxStrength() );
        setReference( magnetModel.getStrength() );
        setExponents( DEFAULT_MIN_EXPONENT, DEFAULT_MAX_EXPONENT );
        setThreshold( DEFAULT_THRESHOLD );
    }
    
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        setReferenceRange( _magnetModel.getMinStrength(), _magnetModel.getMaxStrength() );
        setReference( _magnetModel.getStrength() );
    }
}
