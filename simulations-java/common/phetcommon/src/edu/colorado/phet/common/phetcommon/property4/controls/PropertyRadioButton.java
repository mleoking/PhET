// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.property4.PropertyChangeEvent;
import edu.colorado.phet.common.phetcommon.property4.PropertyChangeListener;
import edu.colorado.phet.common.phetcommon.property4.SettableProperty;

/**
 * PropertyRadioButton wires up a JRadioButton to a property of type T in an enum-style Property<T>.
 *
 * @param <T> the type of object to be selected from
 * @author Chris Malley
 */
public class PropertyRadioButton<T> extends JRadioButton {

    private final SettableProperty<T> property;
    private final PropertyChangeListener<T> listener;

    public PropertyRadioButton( String title, final SettableProperty<T> property, final T value ) {
        super( title );

        this.property = property;

        // update the model when the check box changes
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( value );
            }
        } );

        // update the check box when the model changes
        listener = new PropertyChangeListener<T>() {
            public void propertyChanged( PropertyChangeEvent<T> event ) {
                setSelected( event.value == value );
            }
        };
        property.addListener( listener );

        // default state
        setSelected( property.getValue() == value );
    }

    public void cleanup() {
        property.removeListener( listener );
    }

    public static void main( String[] args ) {

        String name1 = "Curly";
        String name2 = "Larry";
        String name3 = "Moe";
        SettableProperty<String> name = new SettableProperty<String>( name1 ) {{
            addListener( new PropertyChangeListener() {
                public void propertyChanged( PropertyChangeEvent event ) {
                    System.out.println( "name: " + event.toString() );
                }
            } );
        }};


        JPanel panel = new JPanel();
        panel.add( new PropertyRadioButton( name1, name, name1 ) );
        panel.add( new PropertyRadioButton( name2, name, name2 ) );
        panel.add( new PropertyRadioButton( name3, name, name3 ) );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}