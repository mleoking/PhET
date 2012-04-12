// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.DecimalFormat;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings;
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

        //Compute the tick marks in the specified units
        final double lowGravity = density.siToUnit( 1 );
        final double earthGravity = density.siToUnit( 9.8 );
        final double highGravity = density.siToUnit( 20 );

        //Create the slider
        final SliderControl sliderControl = new SliderControl( FPAFSimSharing.UserComponents.fluidDensityControl, Strings.GRAVITY, density.getAbbreviation(), lowGravity, highGravity,
                                                               new ScaledDoubleProperty( module.model.gravity, density.siToUnit( 1.0 ) ), new HashMap<Double, String>() {{
            put( lowGravity, Strings.LOW );
            put( earthGravity, Strings.EARTH );
            put( highGravity, Strings.HIGH );
        }}, new DecimalFormat( "0.0" ) ) {{
            maximized.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( maximized.get() );
                }
            } );
        }};
        addChild( sliderControl );
    }
}
