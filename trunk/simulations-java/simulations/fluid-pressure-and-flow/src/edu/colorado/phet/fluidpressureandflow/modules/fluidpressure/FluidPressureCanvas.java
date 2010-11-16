package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowControlPanel;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas {
    private FluidPressureModule module;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( module );
        this.module = module;

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.lightGray ) );//so earth doesn't bleed through transparent pool
        addChild( new PoolHeightReadoutNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getDistanceUnitProperty() ) );
        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }

        //Some nodes go behind the pool so that it looks like they submerge
        addChild( new FluidPressureAndFlowRulerNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getRulerVisibleProperty() ) );
        final PoolNode poolNode = new PoolNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getLiquidDensityProperty() );

        addChild( poolNode );
        // Control Panel
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( new FluidPressureAndFlowControlPanel( module ) );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.darkGray ) );
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );
//
//        //Reset all button
        addChild( new ButtonNode( "Reset all", (int) ( FluidPressureAndFlowControlPanel.CONTROL_FONT.getSize() * 1.3 ), FluidPressureAndFlowControlPanel.BACKGROUND, FluidPressureAndFlowControlPanel.FOREGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        final Property<Boolean> fluidDensityControlVisible = new Property<Boolean>( false );
        final Property<Boolean> gravityControlVisible = new Property<Boolean>( true );
        final SliderControl fluidDensityControl = new SliderControl( FluidPressureAndFlowModel.GASOLINE_DENSITY, FluidPressureAndFlowModel.HONEY_DENSITY, module.getFluidPressureAndFlowModel().getLiquidDensityProperty(), new HashMap<Double, TickLabel>() {{
            put( FluidPressureAndFlowModel.GASOLINE_DENSITY, new TickLabel( "gasoline" ) );
            put( FluidPressureAndFlowModel.WATER_DENSITY, new TickLabel( "water" ) );
            put( FluidPressureAndFlowModel.HONEY_DENSITY, new TickLabel( "honey" ) );
        }} ) {{
            setOffset( poolNode.getFullBounds().getMinX() - getFullBounds().getWidth() - 2, poolNode.getFullBounds().getMaxY() - getFullBounds().getHeight() );
            fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( fluidDensityControlVisible.getValue() );
                }
            } );
        }};

        final SliderControl gravityControl = new SliderControl( FluidPressureAndFlowModel.MOON_GRAVITY, FluidPressureAndFlowModel.JUPITER_GRAVITY, module.getFluidPressureAndFlowModel().getGravityProperty(), new HashMap<Double, TickLabel>() {{
            put( FluidPressureAndFlowModel.EARTH_GRAVITY, new TickLabel( "Earth" ) );
            put( FluidPressureAndFlowModel.MOON_GRAVITY, new TickLabel( "Moon" ) );
            put( FluidPressureAndFlowModel.JUPITER_GRAVITY, new TickLabel( "Jupiter" ) );
        }} ) {{
            gravityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( gravityControlVisible.getValue() );
                }
            } );
        }};

        addChild( gravityControl );
        addChild( fluidDensityControl );
        final PSwing fluidDensityExpander = new PSwing( new JButton( "Fluid Density >" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fluidDensityControlVisible.setValue( true );
                }
            } );
        }} ) {{
            fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !fluidDensityControlVisible.getValue() );
                }
            } );
            setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
        }};
        addChild( fluidDensityExpander );
        gravityControl.setOffset( fluidDensityExpander.getFullBounds().getMinX(), fluidDensityExpander.getFullBounds().getY() - gravityControl.getFullBounds().getHeight() - 20 );
        addChild( new PSwing( new JButton( "Fluid Density <" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fluidDensityControlVisible.setValue( false );
                }
            } );
        }} ) {{
            fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( fluidDensityControlVisible.getValue() );
                }
            } );
            setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );
    }
}
