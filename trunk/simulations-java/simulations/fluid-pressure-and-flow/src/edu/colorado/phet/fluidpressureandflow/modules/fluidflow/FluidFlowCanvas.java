package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas {
    private PNode particleLayer;
    private PNode foodColoringLayer;

    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( module );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
//        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool

        addChild( new PipeNode( transform, module.getFluidFlowModel().getPipe() ) );
        particleLayer = new PNode();
        foodColoringLayer = new PNode();
        addChild( foodColoringLayer );
        addChild( particleLayer );
        for ( final Particle p : module.getFluidFlowModel().getParticles() ) {
            addParticleNode( p );
        }
        module.getFluidFlowModel().addParticleAddedObserver( new Function1<Particle, Void>() {
            public Void apply( Particle particle ) {
                addParticleNode( particle );
                return null;//TODO: better support for void
            }
        } );
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor0(), module.getFluidPressureAndFlowModel().getPool() ) );
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor1(), module.getFluidPressureAndFlowModel().getPool() ) );
        addChild( new VelocitySensorNode( transform, module.getFluidFlowModel().getVelocitySensor() ) );

        final DropperNode dropperNode = new DropperNode( transform, module.getFluidFlowModel().getPipe(), module.getFluidFlowModel().getDropperOnProperty() );
        addChild( dropperNode );
        addChild( new PSwing( new JButton( "Pour Food Coloring" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getFluidFlowModel().pourFoodColoring();
                }
            } );
        }} ) {{
            setOffset(dropperNode.getFullBounds().getMaxX(),dropperNode.getFullBounds().getMaxY()-getFullBounds().getHeight());
        }} );

        module.getFluidFlowModel().addFoodColoringObserver( new Function1<FoodColoring, Void>() {
            public Void apply( FoodColoring foodColoring ) {
                addFoodColoringNode( foodColoring );
                return null;
            }
        } );
        //Some nodes go behind the pool so that it looks like they submerge
//        addChild( new FluidPressureAndFlowRulerNode( transform, module.getFluidPressureAndFlowModel().getPool() ) );

//        final PoolNode poolNode = new PoolNode( transform, module.getFluidPressureAndFlowModel().getPool() );
//        addChild( poolNode );

        // Control Panel
//        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
//            final PSwing controlPanelPSwing = new PSwing( new FluidPressureAndFlowControlPanel( module ) );
//            addChild( controlPanelPSwing );
//            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.darkGray ) );
//            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
//        }};
//        addChild( controlPanelNode );
////
////        //Reset all button
//        addChild( new ButtonNode( "Reset all", (int) ( FluidPressureAndFlowControlPanel.CONTROL_FONT.getSize() * 1.3 ), FluidPressureAndFlowControlPanel.BACKGROUND, FluidPressureAndFlowControlPanel.FOREGROUND ) {{
//            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
//            addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    module.resetAll();
//                }
//            } );
//        }} );

//        final Property<Boolean> fluidDensityControlVisible = new Property<Boolean>( false );
//        final PSwing fluidDensityControl = new PSwing( new LinearValueControl( 500, 1500, module.getFluidPressureAndFlowModel().getPool().getLiquidDensity(), "Fluid density", "0.00", "kg/m^3" ) {{
//            makeTransparent( this );
//            addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    module.getFluidPressureAndFlowModel().getPool().setLiquidDensity( getValue() );
//                }
//            } );
//            module.getFluidPressureAndFlowModel().getPool().addDensityListener( new SimpleObserver() {
//                public void update() {
//                    setValue( module.getFluidPressureAndFlowModel().getPool().getLiquidDensity() );
//                }
//            } );
//        }} ) {{
//            scale( 1.2 );
////            setOffset( poolNode.getFullBounds().getMinX() - getFullBounds().getWidth() - 2, poolNode.getFullBounds().getMaxY() - getFullBounds().getHeight() );
//            fluidDensityControlVisible.addObserver( new SimpleObserver() {
//                public void update() {
//                    setVisible( fluidDensityControlVisible.getValue() );
//                }
//            } );
//        }};
//        addChild( fluidDensityControl );
//        addChild( new PSwing( new JButton( "Fluid Density >" ) {{
//            addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    fluidDensityControlVisible.setValue( true );
//                }
//            } );
//        }} ) {{
//            fluidDensityControlVisible.addObserver( new SimpleObserver() {
//                public void update() {
//                    setVisible( !fluidDensityControlVisible.getValue() );
//                }
//            } );
//            setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
//        }} );
//        addChild( new PSwing( new JButton( "Fluid Density <" ) {{
//            addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    fluidDensityControlVisible.setValue( false );
//                }
//            } );
//        }} ) {{
//            fluidDensityControlVisible.addObserver( new SimpleObserver() {
//                public void update() {
//                    setVisible( fluidDensityControlVisible.getValue() );
//                }
//            } );
//            setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
//        }} );
    }

    private void addFoodColoringNode( final FoodColoring p ) {
        final FoodColoringNode node = new FoodColoringNode( transform, p );
        foodColoringLayer.addChild( node );
        p.addRemovalListener( new SimpleObserver() {
            public void update() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
            }
        } );
    }

    private void addParticleNode( final Particle p ) {
        final ParticleNode node = new ParticleNode( transform, p );
        particleLayer.addChild( node );
        p.addRemovalListener( new Function0() {
            public Object apply() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
                return null;//TODO: better interface so we don't have to do this
            }
        } );
    }

}
