// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * JCheckBoxMenuItem that is wired to a Property<Boolean>.
 *
 * @author Sam Reid
 */
public class PropertyCheckBoxMenuItem extends JCheckBoxMenuItem {

    private final SettableProperty<Boolean> property;
    private final SimpleObserver propertyObserver;

    public PropertyCheckBoxMenuItem( String text, final SettableProperty<Boolean> property ) {
        super( text );

        this.property = property;

        // update the model when the menu item changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                property.setValue( isSelected() );
            }
        } );

        // update the menu item when the model changes
        propertyObserver = new SimpleObserver() {
            public void update() {
                setSelected( property.getValue() );
            }
        };
        property.addObserver( propertyObserver );
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}
