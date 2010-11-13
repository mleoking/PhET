package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.fluidpressureandflow.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.view.GroundNode;
import edu.colorado.phet.fluidpressureandflow.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.view.SkyNode;

/**
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas {
    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( module );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
//        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor0(), module.getFluidPressureAndFlowModel().getPool() ) );
        addChild( new PressureSensorNode( transform, module.getFluidPressureAndFlowModel().getPressureSensor1(), module.getFluidPressureAndFlowModel().getPool() ) );

        addChild( new PipeNode( transform, module.getFluidFlowModel().getPipe() ) );
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
}
