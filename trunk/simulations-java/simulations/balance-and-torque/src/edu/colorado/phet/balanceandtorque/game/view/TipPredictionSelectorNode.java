// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.game.model.TipPrediction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
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

    public TipPredictionSelectorNode( Property<BalanceGameModel.GameState> gameStateProperty ) {
        PNode panelContents = new HBox( new TipPredictionSelectionPanel( BalanceAndTorqueResources.Images.PLANK_TIPPED_LEFT, TipPrediction.TIP_DOWN_ON_LEFT_SIDE, tipPredictionProperty, gameStateProperty ),
                                        new TipPredictionSelectionPanel( BalanceAndTorqueResources.Images.PLANK_BALANCED, TipPrediction.STAY_BALANCED, tipPredictionProperty, gameStateProperty ),
                                        new TipPredictionSelectionPanel( BalanceAndTorqueResources.Images.PLANK_TIPPED_RIGHT, TipPrediction.TIP_DOWN_ON_RIGHT_SIDE, tipPredictionProperty, gameStateProperty ) );
        addChild( new ControlPanelNode( panelContents ) );
    }

    /**
     * Class that defines a single selection panel.
     */
    private static class TipPredictionSelectionPanel extends PNode {
        private static final double PANEL_WIDTH = 220; // In screen coords, fairly close to pixels.
        private static final Color NON_HIGHLIGHT_COLOR = Color.BLACK;
        private static final Stroke NON_HIGHLIGHT_STROKE = new BasicStroke( 1 );
        private static final Color SELECTED_HIGHLIGHT_COLOR = new Color( 255, 215, 0 );
        private static final Stroke SELECTED_HIGHLIGHT_STROKE = new BasicStroke( 6 );
        private static final Color CORRECT_ANSWER_HIGHLIGHT_COLOR = new Color( 0, 255, 0 );
        private static final Stroke CORRECT_ANSWER_HIGHLIGHT_STROKE = new BasicStroke( 6 );
        protected PPath outline;

        private TipPredictionSelectionPanel( Image image, final TipPrediction correspondingPrediction, final Property<TipPrediction> tipPredictionProperty,
                                             final Property<BalanceGameModel.GameState> gameStateProperty ) {

            // Create and add the panel that represents this tip prediction choice.
            PNode panel = new PImage( image );
            panel.setScale( PANEL_WIDTH / panel.getFullBoundsReference().width );
            addChild( panel );

            // Set up mouse listener that watches to see if the user has
            // selected this option.
            panel.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    tipPredictionProperty.set( correspondingPrediction );
                }
            } );

            // Set up the listener that will highlight or un-highlight the panel.
            outline = new PhetPPath( panel.getFullBoundsReference().getBounds2D(), NON_HIGHLIGHT_STROKE, NON_HIGHLIGHT_COLOR );
            addChild( outline );

            // Add listener for changes to the tip prediction.
            tipPredictionProperty.addObserver( new VoidFunction1<TipPrediction>() {
                public void apply( TipPrediction predictionValue ) {
                    // Turn the highlight on or off.
                    updateHighlightState( predictionValue == correspondingPrediction, gameStateProperty.get() == BalanceGameModel.GameState.DISPLAYING_CORRECT_ANSWER );
                }
            } );

            // Add listener for changes to the game state.
            gameStateProperty.addObserver( new VoidFunction1<BalanceGameModel.GameState>() {
                public void apply( BalanceGameModel.GameState gameState ) {
                    updateHighlightState( tipPredictionProperty.get() == correspondingPrediction, gameState == BalanceGameModel.GameState.DISPLAYING_CORRECT_ANSWER );
                }
            } );

            // Set the cursor to look different when the user mouses over it.
            panel.addInputEventListener( new CursorHandler( CursorHandler.HAND ) );
        }

        private void updateHighlightState( boolean selectionMatches, boolean displayingCorrectAnswer ) {
            Color outlineColor = NON_HIGHLIGHT_COLOR;
            Stroke outlineStroke = NON_HIGHLIGHT_STROKE;
            if ( selectionMatches ) {
                if ( displayingCorrectAnswer ) {
                    outlineColor = CORRECT_ANSWER_HIGHLIGHT_COLOR;
                    outlineStroke = CORRECT_ANSWER_HIGHLIGHT_STROKE;
                }
                else {
                    outlineColor = SELECTED_HIGHLIGHT_COLOR;
                    outlineStroke = SELECTED_HIGHLIGHT_STROKE;
                }
            }
            outline.setStrokePaint( outlineColor );
            outline.setStroke( outlineStroke );
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

        canvas.getLayer().addChild( new TipPredictionSelectorNode( new Property<BalanceGameModel.GameState>( BalanceGameModel.GameState.PRESENTING_INTERACTIVE_CHALLENGE ) ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
