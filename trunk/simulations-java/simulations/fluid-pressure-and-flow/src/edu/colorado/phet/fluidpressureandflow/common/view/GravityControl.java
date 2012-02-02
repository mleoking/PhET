// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;

import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;

/**
 * Control that indicates and allows the user to change the density of the fluid in each tab of Fluid Pressure and Flow.
 * The units can be in metric or English.
 *
 * @author Sam Reid
 */
public class GravityControl<T extends FluidPressureAndFlowModel> extends UnitSwitchingControl<T> {
    public GravityControl( final FluidPressureAndFlowModule<T> module ) {
        super( module, new GravityUnitControl<T>( module, ENGLISH.gravity ), new GravityUnitControl<T>( module, METRIC.gravity ) );
    }
}
