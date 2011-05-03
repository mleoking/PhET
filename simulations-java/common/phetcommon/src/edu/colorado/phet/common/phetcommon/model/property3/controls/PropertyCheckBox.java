// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3.controls;

// Copyright 2002-2011, University of Colorado

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property3.Gettable;
import edu.colorado.phet.common.phetcommon.model.property3.Observable0;
import edu.colorado.phet.common.phetcommon.model.property3.Settable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * JCheckBox that is wired to an Observable, Settable, Gettable property.
 *
 * @author Sam Reid
 */
public class PropertyCheckBox<T extends Gettable<Boolean> & Settable<Boolean> & Observable0> extends JCheckBox {
    //The property to observe
    private final T property;
    private final VoidFunction0 propertyObserver;

    public PropertyCheckBox( String text, final T property ) {
        super( text );

        this.property = property;

        // update the model when the check box changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                property.set( isSelected() );
            }
        } );

        // update the check box when the model changes
        propertyObserver = new VoidFunction0() {
            public void apply() {
                setSelected( property.get() );
            }
        };
        property.addObserver( propertyObserver );
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}
