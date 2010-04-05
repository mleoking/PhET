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
class IntegerLabel extends JLabel {
    
    private final NumberFormat format;
    private int value;
    
    public IntegerLabel() {
        this( 0 );
    }
    
    public IntegerLabel( int value ) {
        this( value, new DecimalFormat( "0" ) );
    }
    
    protected IntegerLabel( int value, NumberFormat format ) {
        this.format = format;
        setValue( value );
    }

    public void setValue( int value ) {
        setText( format.format( value ) );
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    /**
     * A label that displays large integer values in scientific notation.
     */
    public static class ScientificIntegerLabel extends IntegerLabel {
        
        public ScientificIntegerLabel() {
            this( 0 );
        }
        
        public ScientificIntegerLabel( int value ) {
            super( value, new DecimalFormat( "0.0E0" ) );
        }
    }
}