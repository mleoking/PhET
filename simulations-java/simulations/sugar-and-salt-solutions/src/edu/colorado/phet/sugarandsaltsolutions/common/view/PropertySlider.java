// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Slider class that wires up a slider to a SettableProperty<Integer>
 *
 * @author Sam Reid
 */
public class PropertySlider extends JSlider {
    public PropertySlider( int min, int max, final SettableProperty<Integer> value ) {
        super( min, max, value.get() );
        value.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer value ) {
                setValue( value );
            }
        } );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                value.set( getValue() );
            }
        } );
    }
}