// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

public class JMEPropertyCheckBox extends JCheckBox {
    private final SettableProperty<Boolean> property;
    private final SimpleObserver propertyObserver;

    public JMEPropertyCheckBox( String text, final SettableProperty<Boolean> property ) {
        super( text );

        this.property = property;

        // update the model when the check box changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JmeUtils.swingLock( new Runnable() {
                    public void run() {
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
