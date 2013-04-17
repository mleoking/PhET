// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.Format;
import java.text.ParseException;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJTextField;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * Formatted text field that binds to a SettableProperty<Double> and is bounded by the specified min and max values.
 * When the user requests a value outside the specified range, the property takes on the closest value in the range.
 *
 * @author Sam Reid
 */
public class DoubleTextField extends SimSharingJTextField {
    private final Format format;
    private final SettableProperty<Double> property;
    private final double min;
    private final double max;

    public DoubleTextField( IUserComponent component, Format format, final SettableProperty<Double> property, final double min, final double max ) {
        super( component );
        this.format = format;
        this.property = property;
        this.min = min;
        this.max = max;

        //When the property changes, set the value of the text field
        property.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                setValue( value );
            }
        } );

        //When the text field edit completes, set the value of the property.
        final ActionListener updateProperty = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double origPropertyValue = property.get();
                final double value = getClampedValue( getParsedValue() );
                property.set( value );

                //If the property didn't change, it won't send update notifications, but we still need to update the view with the clamped value
                //so it doesn't show something outside of the allowed range.
                if ( origPropertyValue == value ) {
                    setValue( value );
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

    private double getClampedValue( double x ) {
        return clamp( min, x, max );
    }

    @Override protected ParameterSet getParameters() {
        return super.getParameters().with( ParameterKeys.value, getClampedValue( getParsedValue() ) );
    }

    private double getParsedValue() {
        try {
            return getValue();
        }
        catch ( ParseException e ) {
            return property.get();
        }
    }

    private void setValue( final double value ) {
        setText( format.format( value ) );
    }

    private double getValue() throws ParseException {
        return parseText();
    }

    private double parseText() throws ParseException {
        final Object result = format.parseObject( getText() );
        return result instanceof Long ? ( (Long) result ).doubleValue() :
               result instanceof Integer ? ( (Integer) result ).doubleValue() :
               (Double) result;
    }
}