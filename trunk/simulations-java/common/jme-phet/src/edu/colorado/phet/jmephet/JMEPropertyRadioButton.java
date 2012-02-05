// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

public class JMEPropertyRadioButton<T> extends SimSharingJRadioButton {
    private final SettableProperty<T> property;
    private final SimpleObserver propertyObserver;

    public JMEPropertyRadioButton( final IUserComponent userComponent, final String text, final SettableProperty<T> property, final T value ) {
        super( userComponent, text );

        this.property = property;

        // update the model when the check box changes
        this.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSelected( true );
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        // TODO: add notification for sim information?
                        property.set( value );
                    }
                } );
            }
        } );

        // update the check box when the model changes
        propertyObserver = new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSelected( property.get() == value );
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
