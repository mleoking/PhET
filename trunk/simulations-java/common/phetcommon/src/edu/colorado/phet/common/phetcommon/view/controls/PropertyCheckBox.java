// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * JCheckBox that is wired to a Property<Boolean>.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyCheckBox extends JCheckBox {

    private final SettableProperty<Boolean> property;
    private final SimpleObserver propertyObserver;

    public PropertyCheckBox( final String text, final SettableProperty<Boolean> property ) {
        super( text );

        this.property = property;

        // update the model when the check box is toggled.  Use ActionListener instead of ChangeListener to suppress multiple events.
        this.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyActionPerformed( PropertyCheckBox.this, property );

                property.set( isSelected() );
            }
        } );

        // update the check box when the model changes
        propertyObserver = new SimpleObserver() {
            public void update() {
                setSelected( property.get() );
            }
        };
        property.addObserver( propertyObserver );
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }

    //Send a message to the sim sharing event collector that the user toggled the check box
    public static void notifyActionPerformed( JCheckBox checkBox, SettableProperty<Boolean> property ) {
        SimSharingEvents.sendEvent( "checkBox", "pressed", param( "text", checkBox.getText() ), param( "description", property.getDescriptionOrElseQuestion() ), param( "isSelected", checkBox.isSelected() ) );
    }
}