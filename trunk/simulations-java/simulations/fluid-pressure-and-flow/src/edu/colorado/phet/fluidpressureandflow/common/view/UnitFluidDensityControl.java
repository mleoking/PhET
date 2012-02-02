// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;

/**
 * Fluid density control that works with a certain unit
 *
 * @author Sam Reid
 */
public class UnitFluidDensityControl<T extends FluidPressureAndFlowModel> extends MinimizableControl {
    public UnitFluidDensityControl( final FluidPressureAndFlowModule<T> module, Unit density ) {
        super( module.fluidDensityControlVisible, new FluidDensitySlider<T>( module, density, module.fluidDensityControlVisible ) );
    }
}