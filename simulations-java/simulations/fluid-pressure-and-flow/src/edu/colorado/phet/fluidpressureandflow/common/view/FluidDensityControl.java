// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.*;

/**
 * Control that indicates and allows the user to change the density of the fluid in each tab of Fluid Pressure and Flow.
 * The units can be in metric or English.
 *
 * @author Sam Reid
 */
public class FluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
    private final UnitFluidDensityControl<T> metricControl;//Control when in metric units
    private final UnitFluidDensityControl<T> englishControl;//Control when in English units

    public FluidDensityControl( final FluidPressureAndFlowModule<T> module ) {

        //This Property indicates whether units to be shown for the fluid density control should be in metric
        final Or metricUnits = module.model.units.valueEquals( METRIC ).or( module.model.units.valueEquals( ATMOSPHERES ) );

        //Create and add the metric control, but only show it if the units are in metric
        metricControl = new UnitFluidDensityControl<T>( module, METRIC.density );
        metricUnits.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean metricUnits ) {
                metricControl.setVisible( metricUnits );
            }
        } );
        addChild( metricControl );

        //Create and add the English unit control, but only show it if the units are in English
        englishControl = new UnitFluidDensityControl<T>( module, ENGLISH.density );
        metricUnits.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean metricUnits ) {
                englishControl.setVisible( !metricUnits );
            }
        } );
        addChild( englishControl );
    }

    //Determine how big this control would be when expanded, this is the maximum of the expanded metric and english control sizes
    public Dimension2DDouble getMaximumSize() {
        final Dimension2DDouble metricMaxSize = metricControl.getMaximumSize();
        final Dimension2DDouble englishMaxSize = englishControl.getMaximumSize();
        return new Dimension2DDouble( Math.max( metricMaxSize.getWidth(), englishMaxSize.getWidth() ), Math.max( metricMaxSize.getHeight(), englishMaxSize.getHeight() ) );
    }
}