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
 * IntegerRange is used to describe the range of an integer quantity.
 * This class is immutable.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class IntegerRange {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _min;
    private int _max;
    private int _default;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param min
     * @param max
     * @param defaultValue
     */
    public IntegerRange( int min, int max, int defaultValue ) {
        if ( max < min ) {
            throw new IllegalArgumentException( "max < min" );
        }
        if ( defaultValue < min || defaultValue > max ) {
            throw new IllegalArgumentException( "defaultValue out of range" );
        }
        _min = min;
        _max = max;
        _default = defaultValue;
    }
    
    //----------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the lower bound (minimum) of the range.
     * 
     * @return min
     */
    public int getMin() {
        return _min;
    }
    
    /**
     * Gets the upper bound (maximum) of the range.
     * 
     * @return max
     */
    public int getMax() {
        return _max;
    }
    
    /**
     * Gets the default value.
     * 
     * @return default
     */
    public int getDefault() {
        return _default;
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
    public boolean contains( int value ) {
        return( value >= _min && value <= _max );
    }
    
    /**
     * Gets the length of the range.
     * 
     * @return length of the range
     */
    public int getLength() {
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
