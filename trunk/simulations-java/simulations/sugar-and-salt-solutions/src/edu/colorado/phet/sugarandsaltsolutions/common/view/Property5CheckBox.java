// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.sugarandsaltsolutions.common.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * JCheckBox that is wired to a Property<Boolean>.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @deprecated This will be deleted when property5 is merged into property1
 */
public class Property5CheckBox extends JCheckBox {

    private final SettableProperty<Boolean> property;
    private final SimpleObserver propertyObserver;

    public Property5CheckBox( String text, final SettableProperty<Boolean> property ) {
        super( text );

        this.property = property;

        // update the model when the check box changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
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
}