package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowControlPanel;
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
        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor0(), module.getFluidPressureAndFlowModel().getPool() ) );
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor1(), module.getFluidPressureAndFlowModel().getPool() ) );

        //Some nodes go behind the pool so that it looks like they submerge
        addChild( new FluidPressureAndFlowRulerNode( transform, module.getFluidPressureAndFlowModel().getPool() ,module.getRulerVisibleProperty() ) );
        final PoolNode poolNode = new PoolNode( transform, module.getFluidPressureAndFlowModel().getPool() );

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
        final PSwing fluidDensityControl = new PSwing( new LinearValueControl( 500, 1500, module.getFluidPressureAndFlowModel().getPool().getLiquidDensity(), "Fluid density", "0.00", "kg/m^3" ) {{
            setTickLabels( new Hashtable( ){{
                put( 1000,new JLabel("water" ));
                put(  )
            }});
            makeTransparent( this );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.getFluidPressureAndFlowModel().getPool().setLiquidDensity( getValue() );
                }
            } );
            module.getFluidPressureAndFlowModel().getPool().addDensityListener( new SimpleObserver() {
                public void update() {
                    setValue( module.getFluidPressureAndFlowModel().getPool().getLiquidDensity() );
                }
            } );
        }} ) {{
            scale( 1.2 );
            setOffset( poolNode.getFullBounds().getMinX() - getFullBounds().getWidth() - 2, poolNode.getFullBounds().getMaxY() - getFullBounds().getHeight() );
            fluidDensityControlVisible.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( fluidDensityControlVisible.getValue() );
                }
            } );
        }};
        addChild( fluidDensityControl );
        addChild( new PSwing( new JButton( "Fluid Density >" ) {{
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
        }} );
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
