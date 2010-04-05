/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;

/**
 * A label that displays integer values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class NumberLabel extends JLabel {
    
    private final NumberFormat format;
    private double value;
    
    protected NumberLabel( double value, NumberFormat format ) {
        this.format = format;
        setValue( value );
    }

    public void setValue( double value ) {
        setText( format.format( value ) );
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    public static class IntegerLabel extends NumberLabel {
        
        public IntegerLabel() {
            this( 0 );
        }
        
        public IntegerLabel( double value ) {
            super( value, new DecimalFormat( "0" ) );
        }
    }
    
    /**
     * A label that displays large integer values in scientific notation.
     */
    public static class ScientificIntegerLabel extends NumberLabel {
        
        public ScientificIntegerLabel() {
            this( 0 );
        }
        
        public ScientificIntegerLabel( double value ) {
            super( value, new DecimalFormat( "0.0E0" ) );
        }
    }
}