package edu.colorado.phet.fluidpressureandflow.modules.intro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowControlPanel;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends PhetPCanvas {
    private final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;

    public IntroCanvas( final IntroModule module ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;
        double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();
        transform = new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true );
//        transform = new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() / 2 ), STAGE_SIZE.getHeight() / modelHeight, true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        addChild( new PText( "Hello" ) );
        setBorder( null );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PhetPPath( transform.createTransformedShape( module.getIntroModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool
        addChild( new PressureSensorNode( transform, module.getIntroModel().getPressureSensor0(), module.getIntroModel().getPool() ) );
        addChild( new PressureSensorNode( transform, module.getIntroModel().getPressureSensor1(), module.getIntroModel().getPool() ) );

        //Some nodes go behind the pool so that it looks like they submerge
        addChild( new FluidPressureAndFlowRulerNode( transform, module.getIntroModel().getPool() ) );
        final PoolNode poolNode = new PoolNode( transform, module.getIntroModel().getPool() );

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

        addChild( new PSwing( new LinearValueControl( 500, 1500, module.getIntroModel().getPool().getLiquidDensity(), "Fluid density", "0.00", "kg/m^3" ) {{
            makeTransparent( this );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.getIntroModel().getPool().setLiquidDensity( getValue() );
                }
            } );
            module.getIntroModel().getPool().addDensityListener( new SimpleObserver() {
                public void update() {
                    setValue( module.getIntroModel().getPool().getLiquidDensity() );
                }
            } );
        }} ) {{
            scale( 1.2 );
            setOffset( poolNode.getFullBounds().getMinX() - getFullBounds().getWidth() - 2, poolNode.getFullBounds().getMaxY() - getFullBounds().getHeight() );
        }} );

//
//        addChild( new FloatingClockControlNode( model.getClock(), new Function1<Double, String>() {
//
//            DecimalFormat decimalFormat = new DecimalFormat( "0.0" );
//
//            public String apply( Double aDouble ) {
//                return decimalFormat.format( aDouble ) + " seconds";
//            }
//        } ) {{
//            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
//            final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( WorkEnergyModel.DEFAULT_DT / 2, WorkEnergyModel.DEFAULT_DT * 2, "0.00", model.getClock() ) {{
//                makeTransparent( this );
//                addChangeListener( new ChangeListener() {
//                    public void stateChanged( ChangeEvent e ) {
//                        model.getClock().setDt( getValue() );
//                    }
//                } );
//            }};
//            addChild( new PSwing( timeSpeedSlider ) {{
//                setOffset( -getFullBounds().getWidth(), 0 );
//            }} );
//        }} );
    }

    private void makeTransparent( JComponent component ) {
        if ( !( component instanceof JTextComponent ) ) {
            component.setBackground( new Color( 0, 0, 0, 0 ) );
            component.setOpaque( false );
        }
        for ( Component component1 : component.getComponents() ) {
            if ( component1 instanceof JComponent ) {
                makeTransparent( (JComponent) component1 );
            }
        }
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}
