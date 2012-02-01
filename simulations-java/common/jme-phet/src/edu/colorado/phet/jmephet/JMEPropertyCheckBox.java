// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBox;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

public class JMEPropertyCheckBox extends SimSharingJCheckBox {
    private final SettableProperty<Boolean> property;
    private final SimpleObserver propertyObserver;

    public JMEPropertyCheckBox( IUserComponent userComponent, String text, final SettableProperty<Boolean> property ) {
        super( userComponent, text );

        this.property = property;

        // update the model when the check box changes
        this.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JMEUtils.invoke( new Runnable() {
                    public void run() {

                        //TODO: Simsharing
                        //PropertyCheckBox.notifyActionPerformed( JMEPropertyCheckBox.this, property );

                        property.set( isSelected() );
                    }
                } );
            }
        } );

        // update the check box when the model changes
        propertyObserver = new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSelected( property.get() );
                    }
                } );
            }
        };
        property.addObserver( propertyObserver );
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}
