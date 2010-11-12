package edu.colorado.phet.fluidpressureandflow.modules.intro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowControlPanel;
import edu.colorado.phet.fluidpressureandflow.view.GroundNode;
import edu.colorado.phet.fluidpressureandflow.view.PoolNode;
import edu.colorado.phet.fluidpressureandflow.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.view.SkyNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends PhetPCanvas {
    private final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private final PNode rootNode;

    public IntroCanvas( final IntroModule module ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        transform = new ModelViewTransform2D( new Rectangle2D.Double( -5.1, -5.1, 10.2, 10.2 ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        addChild( new PText( "Hello" ) );
        setBorder( null );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PressureSensorNode( transform, module.getIntroModel().getPressureSensor() ) );
        addChild( new PoolNode( transform, module.getIntroModel().getPool() ) );

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

//    private void makeTransparent( JComponent component ) {
//        component.setBackground( new Color( 0, 0, 0, 0 ) );
//        component.setOpaque( false );
//        for ( Component component1 : component.getComponents() ) {
//            if ( component1 instanceof JComponent ) {
//                makeTransparent( (JComponent) component1 );
//            }
//        }
//    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}
