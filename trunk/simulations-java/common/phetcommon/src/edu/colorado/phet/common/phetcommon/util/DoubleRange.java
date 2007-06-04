/* Copyright 2006, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

/**
 * DoubleRange is used to describe the range of a double precision quantity.
 * This class is immutable.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DoubleRange {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _min;
    private final double _max;
    private final double _default;
    private final int _significantDecimalPlaces;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a DoubleRange where all decimal places are significant.
     * 
     * @param min
     * @param max
     * @param defaultValue
     */
    public DoubleRange( double min, double max, double defaultValue ) {
        this( min, max, defaultValue, Integer.MAX_VALUE );
    }
    
    /**
     * Constructor.
     * 
     * @param min
     * @param max
     * @param defaultValue
     * @param significantDecimalPlaces
     */
    public DoubleRange( double min, double max, double defaultValue, int significantDecimalPlaces ) {
        if ( ! ( min <= max ) ) {
            throw new IllegalArgumentException( "min must be <= max" );
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
    
    /**
     * Copy constructor.
     * @param range
     */
    public DoubleRange( DoubleRange range ) {
        this( range.getMin(), range.getMax(), range.getDefault(), range.getSignificantDecimalPlaces() );
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
     * Integer.MAX_VALUE means that all decimal places are significant.
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
