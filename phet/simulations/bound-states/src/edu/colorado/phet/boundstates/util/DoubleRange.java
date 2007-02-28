/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.util;

/**
 * DoubleRange is used to describe the range of a double precision quantity.
 * This class is immutable.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DoubleRange {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _min;
    private double _max;
    private double _default;
    private int _significantDecimalPlaces;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param min
     * @param max
     * @param defaultValue
     * @param significantDecimalPlaces
     */
    public DoubleRange( double min, double max, double defaultValue, int significantDecimalPlaces ) {
        if ( max < min ) {
            throw new IllegalArgumentException( "max < min" );
        }
        if ( defaultValue < min || defaultValue > max ) {
            throw new IllegalArgumentException( "defaultValue out of range" );
        }
        if ( significantDecimalPlaces < 0 ) {
            throw new IllegalArgumentException( "significantDecimalPlaces < 0: " + significantDecimalPlaces );
        }
        _min = min;
        _max = max;
        _default = defaultValue;
        _significantDecimalPlaces = significantDecimalPlaces;
    }
    
    //----------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the lower bound (minimum) of the range.
     * 
     * @return min
     */
    public double getMin() {
        return _min;
    }
    
    /**
     * Gets the upper bound (maximum) of the range.
     * 
     * @return max
     */
    public double getMax() {
        return _max;
    }
    
    /**
     * Gets the default value.
     * 
     * @return default
     */
    public double getDefault() {
        return _default;
    }
    
    /**
     * Gets the number of significant decimal places.
     * Integer.MAX_VALUE should be interpretted to mean that all decimal places are significant.
     * 
     * @return int
     */
    public int getSignificantDecimalPlaces() {
        return _significantDecimalPlaces;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    /**
     * Determines whether the range contains a value.
     * 
     * @param value
     * @return true or false
     */
    public boolean contains( double value ) {
        return( value >= _min && value <= _max );
    }
    
    /**
     * Gets the length of the range.
     * 
     * @return length of the range
     */
    public double getLength() {
        return _max - _min;
    }
    
    /**
     * Is the length of the range zero?
     * @return true or false
     */
    public boolean isZero() {
        return ( _max - _min == 0 );
    }
}
