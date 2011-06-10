// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Displays a value of the form "4x10^3".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TimesTenValueNode extends HTMLNode {

    private static final String PATTERN_VALUE = "<html>{0}x10<sup>{1}</sup></html>";

    private final NumberFormat mantissaFormat;
    private int exponent;
    private final String units;
    private double value;

    public TimesTenValueNode( NumberFormat mantissaFormat, int exponent, String units, double value, Font font, Color color ) {
        setFont( font );
        setHTMLColor( color );
        this.mantissaFormat = mantissaFormat;
        this.exponent = exponent;
        this.units = units;
        this.value = value;
        update();
    }

    public void setValue( double value ) {
        if ( value != this.value ) {
            this.value = value;
            update();
        }
    }

    public void setExponent( int maxExponent ) {
        if ( maxExponent != this.exponent ) {
            this.exponent = maxExponent;
            update();
        }
    }

    private void update() {
        String mantissaString = "0";
        if ( value != 0 ) {
            double mantissa = value / Math.pow( 10, exponent );
            mantissaString = MessageFormat.format( PATTERN_VALUE, mantissaFormat.format( mantissa ), exponent );
        }
        setHTML( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, mantissaString, units ) );
    }
}
