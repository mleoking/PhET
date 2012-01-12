// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.umd.cs.piccolo.nodes.PText;

/**
 * A PText node that displays numbers in a specified format.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class NumberNode extends PText {
    
    private final NumberFormat format;
    private double value;
    
    protected NumberNode( double value, NumberFormat format ) {
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
    
    /**
     * A NumberNode that displays integer values.
     */
    public static class IntegerNode extends NumberNode {
        
        public IntegerNode() {
            this( 0 );
        }
        
        public IntegerNode( double value ) {
            super( value, new DecimalFormat( "0" ) );
        }
    }
    
    /**
     * A NumberNode that displays large integer values in scientific notation.
     */
    public static class ScientificIntegerNode extends NumberNode {
        
        public ScientificIntegerNode() {
            this( 0 );
        }
        
        public ScientificIntegerNode( double value ) {
            super( value, new DecimalFormat( "0.0E0" ) );
        }
    }
}