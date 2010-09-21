/* Copyright 2010, University of Colorado */

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
    
    private double value; // the value
    private String label;  // label that describes the value
    private NumberFormat valueFormat;  // how to format the number for display
    private String units; // the value's units
    private String labelValueUnitsPattern; // pattern that determines the order of label, value and units
    private String notANumber; // what to display if the value is not a number
    private boolean showSign;
    
    public DoubleDisplayNode( double value, String label, NumberFormat valueFormat, String units, String labelValueUnitsPattern, String notANumber, boolean showSign ) {
        this.value = value;
        this.label = label;
        this.valueFormat = valueFormat;
        this.units = units;
        this.labelValueUnitsPattern = labelValueUnitsPattern;
        this.notANumber = notANumber;
        this.showSign = showSign;
        update();
    }
    
    public void setValue( double value ) {
        if ( value != this.value ) {
            this.value = value;
            update();
        }
    }
    
    public double getValue() {
        return value;
    }
    
    public void setLabel( String label ) {
        this.label = label;
        update();
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setValueFormat( NumberFormat valueFormat ) {
        this.valueFormat = valueFormat;
        update();
    }
    
    public NumberFormat getValueFormat() {
        return valueFormat;
    }
    
    public void setUnits( String units ) {
        this.units = units;
        update();
    }
    
    public String getUnits() {
        return units;
    }
    
    public void setLabelValueUnitsPattern( String pattern ) {
        this.labelValueUnitsPattern = pattern;
        update();
    }
    
    public String getLabelValueUnitsPattern() {
        return labelValueUnitsPattern;
    }
    
    public void setNotANumber( String notANumber ) {
        this.notANumber = notANumber;
        update();
    }
    
    public String getNotANumber() {
        return notANumber;
    }
    
    public void setShowSign( boolean showSign ) {
        this.showSign = showSign;
        update();
    }
    
    public boolean isShowSign() {
        return showSign;
    }
    
    private void update() {
        String valueString = null;
        if ( Double.isNaN( value ) ) {
            valueString = notANumber;
        }
        else {
            if ( showSign ) {
                valueString = ( value < 0 ) ? "-" : "+";
            }
            valueString = valueFormat.format( value ); // eg, "1.234"
        }
        String displayString = MessageFormat.format( labelValueUnitsPattern, label, valueString, units ); // eg, "voltage: 1.234 V"
        setHTML( displayString );
    }
}
