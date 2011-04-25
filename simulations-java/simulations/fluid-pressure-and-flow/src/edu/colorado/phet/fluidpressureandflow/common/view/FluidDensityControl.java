// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode.BUTTON_LEFT;
import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;
import static edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureControlPanel.CONTROL_FONT;
import static java.awt.Color.black;

/**
 * Control that indicates and allows the user to change the density of the fluid in each tab of Fluid Pressure and Flow.
 * The units can be in metric or English.
 *
 * @author Sam Reid
 */
public class FluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
    public FluidDensityControl( final FluidPressureAndFlowModule<T> module ) {
        //This Property indicates whether units are in metric or not.
        final ValueEquals<UnitSet> metricUnits = new ValueEquals<UnitSet>( module.getFluidPressureAndFlowModel().units, METRIC );

        //Create and add the metric control, but only show it if the units are in metric
        addChild( new UnitFluidDensityControl<T>( module, METRIC.density ) {{
            metricUnits.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean metricUnits ) {
                    setVisible( metricUnits );
                }
            } );
        }} );

        //Create and add the English unit control, but only show it if the units are in English
        addChild( new UnitFluidDensityControl<T>( module, ENGLISH.density ) {{
            metricUnits.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean metricUnits ) {
                    setVisible( !metricUnits );
                }
            } );
        }} );
    }

    public static class UnitFluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
        public UnitFluidDensityControl( final FluidPressureAndFlowModule<T> module, Unit density ) {
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
                        setVisible( module.fluidDensityControlVisible.getValue() );
                    }
                } );
            }};

            //Button for showing/hiding the slider
            MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( FLUID_DENSITY, BUTTON_LEFT, CONTROL_FONT, black, 10 ) {{
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

                //The default green + button is invisible against the green ground, so use a blue one instead
                setMaximizeImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "maximizeButtonBlue.png" ) );
            }};

            //Add children
            addChild( fluidDensityControl );
            addChild( minimizeMaximizeNode );
        }
    }
}
