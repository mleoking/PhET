// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * JCheckBox that is wired to a Property<Boolean>.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyCheckBox extends JCheckBox {

    public PropertyCheckBox( String text, final Property<Boolean> booleanProperty ) {
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
