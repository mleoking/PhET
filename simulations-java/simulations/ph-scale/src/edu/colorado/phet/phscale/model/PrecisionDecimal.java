/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * PrecisionDecimal is value that is constrained to some specified number of decimal places.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecisionDecimal {
    
    private final double _preciseValue; // the precise value
    private final BigDecimal _bigDecimal; // delegate for constraining precision
    
    /**
     * Constructor
     * 
     * @param value
     * @param numberOfDecimalPlaces
     */
    public PrecisionDecimal( double value, int numberOfDecimalPlaces ) {
        if ( numberOfDecimalPlaces < 0 ) {
            throw new IllegalArgumentException( "numberOfDecimalPlaces must be >= 0" );
        }
        // save the precise value
        _preciseValue = value;
        // format the value as a string with the specified number of decimal places
        DecimalFormat format = createFormat( numberOfDecimalPlaces );
        String stringValue = format.format( value );
        // provide the string to BigDecimal, which will use the string to determine how precision is constrained
        _bigDecimal = new BigDecimal( stringValue );
    }
    
    /*
     * Creates a formatter for numbers.
     */
    private static DecimalFormat createFormat( int numberOfDecimalPlaces ) {
        String pattern = "0";
        for ( int i = 0; i < numberOfDecimalPlaces; i++ ) {
            if ( i == 0 ) {
                pattern += ".";
            }
            pattern += "0";
        }
        return new DefaultDecimalFormat( pattern );
    }
    
    /**
     * Gets the value, constrained to the number of significant decimal places
     * indicated in the constructor.
     * 
     * @return
     */
    public double getValue() {
        return _bigDecimal.doubleValue();
    }
    
    /**
     * Gets the precise value, as provided to the constructor.
     * @return
     */
    public double getPreciseValue() {
        return _preciseValue;
    }
    
    /**
     * Gets the number of significant decimal places in values 
     * returned by getValue.
     * 
     * @return
     */
    public int getNumberOfDecimalPlaces() {
        return _bigDecimal.scale();
    }
    
    /**
     * Test & examples.
     */
    public static void main( String[] args ) {
        
        PrecisionDecimal d1 = new PrecisionDecimal( 12.345678, 2 );
        System.out.println( d1.getPreciseValue() + " to " + d1.getNumberOfDecimalPlaces() + " places = " + d1.getValue() );
        
        PrecisionDecimal d2 = new PrecisionDecimal( 9.876543210, 3 );
        System.out.println( d2.getPreciseValue() + " to " + d2.getNumberOfDecimalPlaces() + " places = " + d2.getValue() );
        
        PrecisionDecimal d3 = new PrecisionDecimal( -1.456789, 1 );
        System.out.println( d3.getPreciseValue() + " to " + d3.getNumberOfDecimalPlaces() + " places = " + d3.getValue() );
    }
}
