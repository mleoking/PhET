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
 * The reference is the magnetic field of a magnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridRescaler implements IRescaler  {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double THRESHOLD = 0.8;
    private static final double EXPONENT = 0.5;
    
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
    
    public void setThreshold( double threshold ) {
        assert( threshold >= 0 && threshold <= 1 );
        _threshold = threshold;
    }
    
    public double getThresold() {
        return _threshold;
    }
    
    public void setExponent( double exponent ) {
        assert( exponent > 0 && exponent <= 1.0 );
        _exponent = exponent;
    } 
    
    public double getExponent() {
        return _exponent;
    }
    
    //----------------------------------------------------------------------------
    // IRescaler implementation
    //----------------------------------------------------------------------------
    
    public double rescale( double scale ) {
        assert( scale >=0 && scale <= 1 );
        double newScale = scale;
        if ( scale != 0 && scale <= _threshold ) {
            newScale = Math.pow( scale / _threshold, _exponent );
            newScale = MathUtil.clamp( 0, newScale, 1 );
        }
        return newScale;
    }
}
