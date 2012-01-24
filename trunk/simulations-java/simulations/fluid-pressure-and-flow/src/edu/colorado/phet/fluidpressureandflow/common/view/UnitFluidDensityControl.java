// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.ScaledDoubleProperty;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode.BUTTON_LEFT;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel.*;
import static java.awt.Color.black;

/**
 * Fluid density control that works with a certain unit
 *
 * @author Sam Reid
 */
public class UnitFluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
    private final Property<Boolean> fluidDensityControlVisible;

    public UnitFluidDensityControl( final FluidPressureAndFlowModule<T> module, Unit density ) {
        fluidDensityControlVisible = module.fluidDensityControlVisible;

        //Compute the tick marks in the specified units
        final double gasDensity = density.siToUnit( GASOLINE_DENSITY );
        final double honeyDensity = density.siToUnit( HONEY_DENSITY );
        final double waterDensity = density.siToUnit( WATER_DENSITY );

        //Create the slider
        final SliderControl fluidDensityControl = new SliderControl( UserComponents.fluidDensitySlider, FLUID_DENSITY, density.getAbbreviation(), gasDensity, honeyDensity,
                                                                     new ScaledDoubleProperty( module.model.liquidDensity, density.siToUnit( 1.0 ) ), new HashMap<Double, String>() {{
            put( gasDensity, GASOLINE );
            put( waterDensity, WATER );
            put( honeyDensity, HONEY );
        }} ) {{
            module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( fluidDensityControlVisible.get() );
                }
            } );
        }};

        //Button for showing/hiding the slider
        MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( FLUID_DENSITY, BUTTON_LEFT, FluidPressureCanvas.CONTROL_FONT, black, 10 ) {{
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.fluidDensityControlVisible.set( isMaximized() );
                }
            } );
            module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setMaximized( module.fluidDensityControlVisible.get() );
                }
            } );
            translate( 0, -getFullBounds().getHeight() );
        }};

        //Add children
        addChild( new NoBoundsWhenInvisible( fluidDensityControl ) );
        addChild( minimizeMaximizeNode );
    }

    //Find the size of this component when the slider is visible
    public Dimension2DDouble getMaximumSize() {
        //Store the original value
        boolean visible = fluidDensityControlVisible.get();

        //Enable the slider visibility
        fluidDensityControlVisible.set( true );

        //Find the value we were looking for
        Dimension2DDouble size = new Dimension2DDouble( getFullBounds().getWidth(), getFullBounds().getHeight() );

        //Restore the old value; single threaded so nobody will notice a flicker
        fluidDensityControlVisible.set( visible );

        //return the size of this component when slider is showing
        return size;
    }
}