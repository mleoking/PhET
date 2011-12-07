// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.balanceandtorque.game.model.TipPrediction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that defines a user interface element that allows the user to select
 * one of three possible ways in which the balance might behave - tip left,
 * tip right, or stay balanced.
 *
 * @author John Blanco
 */
public class TipPredictionSelectorNode extends PNode {

    // Property that tracks the selected prediction.
    public Property<TipPrediction> tipPredictionProperty = new Property<TipPrediction>( TipPrediction.NONE );

    public TipPredictionSelectorNode() {
        PNode panelContents = new HBox( new TipPredictionSelectionPanel( "Tip Left", TipPrediction.TIP_DOWN_ON_LEFT_SIDE, tipPredictionProperty ),
                                        new TipPredictionSelectionPanel( "Stay Balanced", TipPrediction.STAY_BALANCED, tipPredictionProperty ),
                                        new TipPredictionSelectionPanel( "Tip Right", TipPrediction.TIP_DOWN_ON_RIGHT_SIDE, tipPredictionProperty ) );
        addChild( new ControlPanelNode( panelContents ) );
    }

    /**
     * Class that defines a single selection panel.
     */
    private static class TipPredictionSelectionPanel extends PNode {
        private static final Color HIGHLIGHT_COLOR = Color.YELLOW;
        private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );
        private static final Stroke HIGHLIGHT_STROKE = new BasicStroke( 3 );

        private TipPredictionSelectionPanel( String text, final TipPrediction correspondingPrediction, final Property<TipPrediction> tipPredictionProperty ) {

            // Create and add the panel that represents this tip prediction choice.
            PNode panel = new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 50 ), Color.PINK, new BasicStroke( 1 ), Color.BLACK );
            panel.addChild( new PText( text ) );
            addChild( panel );

            // Set up mouse listener that watches to see if the user has
            // selected this option.
            panel.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    tipPredictionProperty.set( correspondingPrediction );
                }
            } );

            // Set up the listener that will highlight or un-highlight the panel.
            final PPath highlight = new PhetPPath( panel.getFullBoundsReference().getBounds2D(), HIGHLIGHT_STROKE, INVISIBLE_COLOR );
            addChild( highlight );
            tipPredictionProperty.addObserver( new VoidFunction1<TipPrediction>() {
                public void apply( TipPrediction predictionValue ) {
                    // Turn the highlight on or off.
                    highlight.setStrokePaint( predictionValue == correspondingPrediction ? HIGHLIGHT_COLOR : INVISIBLE_COLOR );
                }
            } );
        }
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 500, 300 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new TipPredictionSelectorNode() );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
