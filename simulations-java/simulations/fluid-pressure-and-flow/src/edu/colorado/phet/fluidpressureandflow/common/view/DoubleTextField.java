// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.Format;
import java.text.ParseException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * Formatted text field that binds to a SettableProperty<Double> and is bounded by the specified min and max values.
 * When the user requests a value outside the specified range, the property takes on the closest value in the range.
 *
 * @author Sam Reid
 */
public class DoubleTextField extends JFormattedTextField {
    public DoubleTextField( Format format, final SettableProperty<Double> property, final double min, final double max ) {
        super( format );
        //When the property changes, set the value of the text field
        property.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                setValue( value );
            }
        } );

        //When the text field edit completes, set the value of the property.
        final ActionListener updateProperty = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    commitEdit();
                    property.set( clamp( min, ( (Number) getValue() ).doubleValue(), max ) );
                }
                catch ( ParseException e1 ) {
                    //If parse error, go back to the true value of the property.
                    setValue( property.get() );
                }
            }
        };
        addActionListener( updateProperty );

        //When clicking out of the text field, set the value of the property
        addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                updateProperty.actionPerformed( null );
            }
        } );
    }
}
