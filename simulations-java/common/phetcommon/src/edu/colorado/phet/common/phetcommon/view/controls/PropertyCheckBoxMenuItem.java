// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * JCheckBoxMenuItem that is wired to a Property<Boolean>.
 *
 * @author Sam Reid
 */
public class PropertyCheckBoxMenuItem extends JCheckBoxMenuItem {

    public PropertyCheckBoxMenuItem( String text, final SettableProperty<Boolean> booleanProperty ) {
        super( text );

        // update the model when the check box changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                booleanProperty.setValue( isSelected() );
            }
        } );

        // update the check box when the model changes
        booleanProperty.addObserver( new SimpleObserver() {
            public void update() {
                setSelected( booleanProperty.getValue() );
            }
        } );
    }
}
