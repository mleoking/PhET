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

import edu.colorado.phet.common.math.MathUtil;


/** 
 * CompassGridRescaler is used to scale the intensity of the needles in the compass grid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridRescaler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double THRESHOLD = 0.8;  // 0 ... 1
    private static final double EXPONENT = 0.5;   // 0.3 ... 0.8
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _threshold;
    private double _exponent;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     */
    public CompassGridRescaler() {
        super();      
        _threshold = THRESHOLD;
        _exponent = EXPONENT;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the threshold.
     * Values greater than the threshold are set to 1.0.
     * Values less than or equal to the threshold are rescaled.
     * 
     * @param threshold (0...1)
     */
    public void setThreshold( double threshold ) {
        assert( threshold >= 0 && threshold <= 1 );
        _threshold = threshold;
    }
    
    /**
     * Gets the threshold.
     * 
     * @return the threshold
     */
    public double getThresold() {
        return _threshold;
    }
    
    /**
     * Sets the exponent.
     * 
     * @param exponent the exponent
     */
    public void setExponent( double exponent ) {
        assert( exponent > 0 && exponent <= 1.0 );
        _exponent = exponent;
    } 
    
    /**
     * Gets the exponent.
     * 
     * @return the exponent
     */
    public double getExponent() {
        return _exponent;
    }
    
    //----------------------------------------------------------------------------
    // Rescale
    //----------------------------------------------------------------------------
    
    /**
     * Rescales a "field strength" value.
     * 
     * @param strength a field strength value, in the range 0...1
     * @return the rescaled field strength, in the range 0...1
     */
    public double rescale( double strength ) {
        assert( strength >=0 && strength <= 1 );
        double newStrength = 1.0;
        if ( strength != 0 && strength <= _threshold ) {
            newStrength = Math.pow( strength / _threshold, _exponent );
//            newStrength = MathUtil.clamp( 0, newStrength, 1 );
        }
        return newStrength;
    }
}
