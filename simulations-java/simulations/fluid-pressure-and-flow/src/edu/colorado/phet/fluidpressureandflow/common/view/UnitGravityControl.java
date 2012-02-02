// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;

/**
 * Gravity control that works with a certain unit
 *
 * @author Sam Reid
 */
public class UnitGravityControl<T extends FluidPressureAndFlowModel> extends MinimizableControl {
    public UnitGravityControl( final FluidPressureAndFlowModule<T> module, Unit density ) {
        super( module.gravityControlVisible, new GravitySlider<T>( module, density, module.gravityControlVisible ) );
    }
}