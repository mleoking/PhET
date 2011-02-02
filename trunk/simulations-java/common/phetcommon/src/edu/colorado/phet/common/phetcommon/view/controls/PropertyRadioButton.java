// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * PropertyRadioButton wires up a JRadioButton to a property of type T in an enum-style Property<T>.
 *
 * @param <T> the type of object to be selected from
 * @author Sam Reid
 * @author Chris Malley
 */
public class PropertyRadioButton<T> extends JRadioButton {
    public PropertyRadioButton( String title, final SettableProperty<T> property, final T value ) {
        super( title );
        final SimpleObserver updateSelected = new SimpleObserver() {
            public void update() {
                setSelected( property.getValue() == value );
            }
        };
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( value );
                updateSelected.update();//make sure radio buttons don't toggle off, in case they're not in a button group
            }
        } );
        property.addObserver( updateSelected );
    }
}