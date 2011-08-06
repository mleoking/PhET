// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.dev;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * LinearValueControl that is wired up to a Property<Double>
 *
 * @author Sam Reid
 */
public class DoublePropertySlider extends LinearValueControl {
    public DoublePropertySlider( String label, double min, double max, final Property<Double> value ) {
        super( min, max, label, "0.00", "" );
        setValue( value.get() );
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double aDouble ) {
                setValue( aDouble );
            }
        } );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                value.set( getValue() );
            }
        } );
    }
}
