// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode.BUTTON_LEFT;
import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;
import static java.awt.Color.black;

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
        //This Property indicates whether units are in metric or not.
        final ValueEquals<UnitSet> metricUnits = new ValueEquals<UnitSet>( module.getFluidPressureAndFlowModel().units, METRIC );

        //Create and add the metric control, but only show it if the units are in metric
        metricControl = new UnitFluidDensityControl<T>( module, METRIC.density ) {{
            metricUnits.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean metricUnits ) {
                    setVisible( metricUnits );
                }
            } );
        }};
        addChild( metricControl );

        //Create and add the English unit control, but only show it if the units are in English
        englishControl = new UnitFluidDensityControl<T>( module, ENGLISH.density ) {{
            metricUnits.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean metricUnits ) {
                    setVisible( !metricUnits );
                }
            } );
        }};
        addChild( englishControl );
    }

    public double getMaximumHeight() {
        return metricControl.getMaximumHeight();
    }

    public static class UnitFluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
        private Property<Boolean> fluidDensityControlVisible;

        public UnitFluidDensityControl( final FluidPressureAndFlowModule<T> module, Unit density ) {
            fluidDensityControlVisible = module.fluidDensityControlVisible;
            //Compute the tick marks in the specified units
            final double gasDensity = density.siToUnit( GASOLINE_DENSITY );
            final double honeyDensity = density.siToUnit( HONEY_DENSITY );
            final double waterDensity = density.siToUnit( WATER_DENSITY );

            //Create the slider
            final SliderControl fluidDensityControl = new SliderControl( FLUID_DENSITY, density.getAbbreviation(), gasDensity, honeyDensity,
                                                                         new ScaledDoubleProperty( module.getFluidPressureAndFlowModel().liquidDensity, density.siToUnit( 1.0 ) ), new HashMap<Double, TickLabel>() {{
                        put( gasDensity, new TickLabel( GASOLINE ) );
                        put( waterDensity, new TickLabel( WATER ) );
                        put( honeyDensity, new TickLabel( HONEY ) );
                    }} ) {{
                module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( fluidDensityControlVisible.getValue() );
                    }
                } );
            }};

            //Button for showing/hiding the slider
            MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( FLUID_DENSITY, BUTTON_LEFT, FluidPressureCanvas.CONTROL_FONT, black, 10 ) {{
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        module.fluidDensityControlVisible.setValue( isMaximized() );
                    }
                } );
                module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                    public void update() {
                        setMaximized( module.fluidDensityControlVisible.getValue() );
                    }
                } );
                translate( 0, -getFullBounds().getHeight() );
            }};

            //Add children
            addChild( new NoBoundsWhenInvisible( fluidDensityControl ) );
            addChild( minimizeMaximizeNode );
        }

        //Find the size of this component when the slider is visible
        public double getMaximumHeight() {
            //Store the original value
            boolean visible = fluidDensityControlVisible.getValue();

            //Enable the slider visibility
            fluidDensityControlVisible.setValue( true );

            //Find the value we were looking for
            final double height = getFullBounds().getHeight();

            //Restore the old value; single threaded so nobody will notice a flicker
            fluidDensityControlVisible.setValue( visible );

            //return the height of this component when slider is showing
            return height;
        }
    }

}