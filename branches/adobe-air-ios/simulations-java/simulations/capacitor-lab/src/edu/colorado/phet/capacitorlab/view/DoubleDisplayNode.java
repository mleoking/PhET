// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Displays a double value.
 * Label, value format, and units can be specific.
 * Order of the label, value and units can also be specified via a MessageFormat pattern.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DoubleDisplayNode extends HTMLNode {

    private final String label;  // label that describes the value
    private final String units; // the value's units
    private final NumberFormat valueFormat;  // how to format the number for display
    private final String labelValueUnitsPattern; // pattern that determines the order of label, value and units
    private final String notANumber; // what to display if the value is not a number

    private double value; // the value

    public DoubleDisplayNode( double value, String label, NumberFormat valueFormat, String units, String labelValueUnitsPattern, String notANumber ) {
        this.value = value;
        this.label = label;
        this.valueFormat = valueFormat;
        this.units = units;
        this.labelValueUnitsPattern = labelValueUnitsPattern;
        this.notANumber = notANumber;
        update();
    }

    public void setValue( double value ) {
        if ( value != this.value ) {
            this.value = value;
            update();
        }
    }

    private void update() {
        String valueString;
        if ( Double.isNaN( value ) ) {
            valueString = notANumber;
        }
        else {
            valueString = valueFormat.format( value ); // eg, "1.234"
        }
        String displayString = MessageFormat.format( labelValueUnitsPattern, label, valueString, units ); // eg, "voltage: 1.234 V"
        setHTML( displayString );
    }
}
