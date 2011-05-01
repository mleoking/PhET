// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.view.FluidPressureCanvas;

/**
 * Radio button used in fluid pressure and flow, which uses the sim-specific font for controls.
 *
 * @author Sam Reid
 */
public class RadioButton<T> extends PropertyRadioButton<T> {
    public RadioButton( String title, final SettableProperty<T> tSettableProperty, final T value ) {
        super( title, tSettableProperty, value );
        setFont( FluidPressureCanvas.CONTROL_FONT );
    }
}
