// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureControlPanel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode.BUTTON_LEFT;
import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel.*;

/**
 * @author Sam Reid
 */
public class FluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
    public FluidDensityControl( final FluidPressureAndFlowModule<T> module ) {
        final SliderControl fluidDensityControl = new SliderControl( FLUID_DENSITY, KG_PER_M_3, GASOLINE_DENSITY, HONEY_DENSITY, module.getFluidPressureAndFlowModel().liquidDensity, new HashMap<Double, TickLabel>() {{
            put( GASOLINE_DENSITY, new TickLabel( GASOLINE ) );
            put( WATER_DENSITY, new TickLabel( WATER ) );
            put( HONEY_DENSITY, new TickLabel( HONEY ) );
        }} ) {{
            module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.fluidDensityControlVisible.getValue() );
                }
            } );
        }};
        MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( FLUID_DENSITY, BUTTON_LEFT, FluidPressureControlPanel.CONTROL_FONT, Color.black, 10 ) {{
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

        addChild( fluidDensityControl );
        addChild( minimizeMaximizeNode );
    }
}
