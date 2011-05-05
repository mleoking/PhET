// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4.controls;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.property4.PropertyChangeEvent;
import edu.colorado.phet.common.phetcommon.property4.PropertyChangeListener;
import edu.colorado.phet.common.phetcommon.property4.SettableProperty;

/**
 * JCheckBox that is wired to a SettableValue<Boolean>.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyCheckBox extends JCheckBox {

    private final SettableProperty<Boolean> property;
    private final PropertyChangeListener<Boolean> listener;

    public PropertyCheckBox( String text, final SettableProperty<Boolean> property ) {
        super( text );

        this.property = property;

        // update the model when the check box changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                property.setValue( isSelected() );
            }
        } );

        // update the check box when the model changes
        listener = new PropertyChangeListener<Boolean>() {
            public void propertyChanged( PropertyChangeEvent<Boolean> event ) {
                setSelected( property.getValue() );
            }
        };
        property.addListener( listener );

        // default state
        setSelected( property.getValue() );
    }

    public void cleanup() {
        property.removeListener( listener );
    }

    public static void main( String[] args ) {

        SettableProperty<Boolean> enabled = new SettableProperty<Boolean>( false ) {{
            addListener( new PropertyChangeListener() {
                public void propertyChanged( PropertyChangeEvent event ) {
                    System.out.println( "enabled=" + event.value );
                }
            } );
        }};

        JPanel panel = new JPanel();
        panel.add( new PropertyCheckBox( "enabled", enabled ) );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
