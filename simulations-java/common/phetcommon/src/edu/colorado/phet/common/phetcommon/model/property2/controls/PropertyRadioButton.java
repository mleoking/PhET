// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property2.Observer;
import edu.colorado.phet.common.phetcommon.model.property2.Property;
import edu.colorado.phet.common.phetcommon.model.property2.UpdateEvent;
import edu.colorado.phet.common.phetcommon.util.Option.None;

/**
 * PropertyRadioButton wires up a JRadioButton to a property of type T in an enum-style Property<T>.
 *
 * @param <T> the type of object to be selected from
 * @author Sam Reid
 * @author Chris Malley
 */
public class PropertyRadioButton<T> extends JRadioButton {
    private final Property<T> property;
    private final Observer<T> propertyObserver;

    public PropertyRadioButton( String title, final Property<T> property, final T value ) {
        super( title );
        this.property = property;

        // update the model when the check box changes
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( value );
                propertyObserver.update( new UpdateEvent<T>( value, new None<T>() ) );//make sure radio buttons don't toggle off, in case they're not in a button group
            }
        } );

        // update the check box when the model changes
        propertyObserver = new Observer<T>() {
            @Override public void update( UpdateEvent<T> e ) {
                setSelected( e.value == value );
            }
        };
        property.addObserver( propertyObserver );
    }

    //Remove the listener to avoid memory leaks
    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}