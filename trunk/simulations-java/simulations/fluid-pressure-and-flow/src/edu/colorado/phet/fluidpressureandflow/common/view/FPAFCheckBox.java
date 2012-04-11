// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;

import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.FOREGROUND;

/**
 * Stylized property check box with colors and fonts used in Fluid Pressure and Flow sim.
 *
 * @author Sam Reid
 */
public class FPAFCheckBox extends PropertyCheckBox {
    public FPAFCheckBox( IUserComponent component, String label, final Property<Boolean> property ) {
        super( component, label, property );
        setForeground( FOREGROUND );
        setFont( FluidPressureCanvas.CONTROL_FONT );
    }
}