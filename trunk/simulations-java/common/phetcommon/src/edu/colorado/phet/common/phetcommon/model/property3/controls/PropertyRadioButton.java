// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3.controls;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property3.GettableSettableObservable0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * JCheckBox that is wired to an Observable, Settable, Gettable property.
 *
 * @author Sam Reid
 */
public class PropertyRadioButton<T> extends JRadioButton {
    //The property to observe
    private final GettableSettableObservable0<T> property;
    private final VoidFunction0 propertyObserver;

    public PropertyRadioButton( String text, final GettableSettableObservable0<T> property, final T value ) {
        super( text );

        this.property = property;

        // update the model when the radio button changes
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( isSelected() ) {
                    property.set( value );
                }
                propertyObserver.apply();//make sure radio buttons don't toggle off, in case they're not in a button group
            }
        } );

        // update the check box when the model changes
        propertyObserver = new VoidFunction0() {
            public void apply() {
                setSelected( property.get() == value );
            }
        };
        property.addObserver( propertyObserver );
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}
