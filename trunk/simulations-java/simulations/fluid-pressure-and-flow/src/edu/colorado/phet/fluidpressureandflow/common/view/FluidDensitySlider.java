// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.DecimalFormat;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.ScaledDoubleProperty;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.fluidDensitySlider;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel.*;

/**
 * The slider in the minimizable fluid density control.
 *
 * @author Sam Reid
 */
public class FluidDensitySlider<T extends FluidPressureAndFlowModel> extends PNode {
    public FluidDensitySlider( final FluidPressureAndFlowModule<T> module, Unit density, final Property<Boolean> maximized ) {

        //Compute the tick marks in the specified units
        final double gasDensity = density.siToUnit( GASOLINE_DENSITY );
        final double honeyDensity = density.siToUnit( HONEY_DENSITY );
        final double waterDensity = density.siToUnit( WATER_DENSITY );

        //Create the slider
        final SliderControl sliderControl = new SliderControl( fluidDensitySlider, FLUID_DENSITY, density.getAbbreviation(), gasDensity, honeyDensity,
                                                               new ScaledDoubleProperty( module.model.liquidDensity, density.siToUnit( 1.0 ) ), new HashMap<Double, String>() {{
            put( gasDensity, GASOLINE );
            put( waterDensity, WATER );
            put( honeyDensity, HONEY );
        }}, new DecimalFormat( "0" ) ) {{
            module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( maximized.get() );
                }
            } );
        }};
        addChild( sliderControl );
    }
}
