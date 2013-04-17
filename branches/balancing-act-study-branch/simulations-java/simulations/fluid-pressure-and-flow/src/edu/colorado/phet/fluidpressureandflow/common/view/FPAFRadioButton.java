// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;

/**
 * Radio button used in fluid pressure and flow, which uses the sim-specific font for controls.
 *
 * @author Sam Reid
 */
public class FPAFRadioButton<T> extends PropertyRadioButton<T> {
    public FPAFRadioButton( IUserComponent component, String title, final SettableProperty<T> tSettableProperty, final T value ) {
        super( component, title, tSettableProperty, value );
        setFont( FluidPressureCanvas.CONTROL_FONT );
    }
}
