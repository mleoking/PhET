// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.ScaledDoubleProperty;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;

/**
 * The slider in the minimizable fluid density control.
 *
 * @author Sam Reid
 */
public class GravitySlider<T extends FluidPressureAndFlowModel> extends PNode {
    public GravitySlider( final FluidPressureAndFlowModule<T> module, final Unit density, final Property<Boolean> maximized ) {

        //Create the slider
        final SliderControl sliderControl = new SliderControl( FPAFSimSharing.UserComponents.gravitySlider, "gravity", "m/s/s", 1, 20,
                                                               new ScaledDoubleProperty( module.model.gravity, density.siToUnit( 1.0 ) ), new HashMap<Double, String>() {{
            put( density.siToUnit( 1.0 ), "low" );
            put( density.siToUnit( 9.8 ), "earth" );
            put( density.siToUnit( 20 ), "high" );
        }} ) {{
            module.gravityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( maximized.get() );
                }
            } );
        }};
        addChild( sliderControl );
    }
}