/* Copyright 2004, University of Colorado */

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
 * DefaultRescaler adjusts a scale to some reference value and range.
 * The amount of adjustment can be configured.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GenericRescaler implements IRescaler {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The reference value.
    private double _reference;
    
    // The maximum value of the reference.
    private double _maxReference;
    
    // Values below this value are rescaled.
    private double _threshold;
    
    // Approach this rescaling exponent as reference value approached zero.
    private double _minExponent;
    
    // Approach this rescaling exponent as reference value approached its maximum.
    private double _maxExponent;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public GenericRescaler() {
        _maxReference = 1.0;
        _reference = _maxReference;
        _threshold = 1.0;
        _minExponent = 0.3;
        _maxExponent = 0.8;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setReference( double reference ) {
        assert ( reference >= 0 && reference <= _maxReference );
        _reference = reference;
    }
    
    public double getReference() {
        return _reference;
    }
    
    public void setMaxReference( double maxReference ) {
        assert( maxReference > 0 );
        _maxReference = maxReference;
        if ( _reference > maxReference ) {
            _reference = maxReference;
        }
    }
    
    public double getMaxReference() {
        return _maxReference;
    }
    
    public void setThreshold( double threshold ) {
        assert( threshold >= 0 && threshold <= 1 );
        _threshold = threshold;
    }
    
    public double getThresold() {
        return _threshold;
    }
    
    public void setExponents( double minExponent, double maxExponent ) {
        assert( minExponent <= maxExponent );
        assert( minExponent > 0 && minExponent <= 1.0 );
        assert( maxExponent > 0 && maxExponent <= 1.0 );
        _minExponent = minExponent;
        _maxExponent = maxExponent;
    } 
    
    public double getMinExponent() {
        return _minExponent;
    }
    
    public double getMaxExponent() {
        return _maxExponent;
    }
 
    //----------------------------------------------------------------------------
    // rescaling methods
    //----------------------------------------------------------------------------
    
    /**
     * Rescales values so (a) they are a bit more linear, and (b) the extent to which 
     * they are rescaled is a function of some reference scale. 
     * <p>
     * The algorithm used is as follows (courtesy of Mike Dubson):
     * <ul>
     * <li>Bo is some threshold value
     * <li>if B > Bo, scale = 1
     * <li>if B <= Bo, scale = (B/Bo)**N
     * <li>exponent N is a function of the reference value
     * </ul>
     * 
     * @param scale a value in the range 0-1 inclusive
     * @return a rescaled value in the range 0-1 inclusive
     */
    public double rescale( double scale ) {
        assert( scale >=0 && scale <= 1 );
        double newScale = 0;
        if ( scale != 0 ) {
            double referenceScale = _reference / _maxReference;
            if ( scale > _threshold ) {
                newScale = referenceScale;
            }
            else {
                double exponent = _maxExponent - ( referenceScale * ( _maxExponent - _minExponent ) );
                newScale = referenceScale * Math.pow( scale / _threshold, exponent );
                newScale = MathUtil.clamp( 0, newScale, 1 );
            }
        }
        return newScale;
    }
}