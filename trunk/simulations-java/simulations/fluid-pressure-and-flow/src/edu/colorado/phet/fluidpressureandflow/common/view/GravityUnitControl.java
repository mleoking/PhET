// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;

/**
 * Gravity control that works with a certain unit
 *
 * @author Sam Reid
 */
public class GravityUnitControl<T extends FluidPressureAndFlowModel> extends MinimizableControl {
    public GravityUnitControl( final FluidPressureAndFlowModule<T> module, Unit gravityUnits ) {
        super( module.gravityControlVisible, new GravitySlider<T>( module, gravityUnits, module.gravityControlVisible ), Strings.GRAVITY );
    }
}
