/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the game tab.
 */
public class GameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final int BUTTONS_FONT_SIZE = 30;
    private static final Color BUTTONS_COLOR = new Color( 255, 255, 0, 150 ); // translucent yellow

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private final GameModel2 model;

    // View
    private final PNode rootNode;

    private final GradientButtonNode checkButton = new GradientButtonNode( RPALStrings.BUTTON_CHECK, BUTTONS_FONT_SIZE, BUTTONS_COLOR );
    private final GameSettingsPanel panel;
    private final PNode gameSettingsNode;
    private final GameScoreboardNode scoreboard = new GameScoreboardNode( GameModel2.MAX_LEVELS, GameModel2.MAX_SCORE, new DecimalFormat( "0.#" ) ) {
        {
            setBackgroundWidth( BuildAnAtomDefaults.STAGE_SIZE.width * 0.85 );
        }
    };

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final GameModel2 model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Background.
        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
        gameSettingsNode = new PSwing( panel );
        panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
            @Override
            public void startButtonPressed() {
                model.startGame();
            }
        } );

        // Set up listener for the button on the score board that indicates that
        // a new game is desired.
        scoreboard.addGameScoreboardListener( new GameScoreboardNode.GameScoreboardListener() {

            public void newGamePressed() {
                model.newGame();

            }
        });

        // TODO: Temp - Put a "TBD" indicator on the canvas.
        //        PText tbdIndicator = new PText( "TBD" );
        //        tbdIndicator.setFont( new PhetFont( 100, true ) );
        //        tbdIndicator.setTextPaint( new Color(0, 0, 0, 100) );
        //        tbdIndicator.setOffset( 380, 50 );
        //        rootNode.addChild( tbdIndicator );

        // Set up the initial state.
        updateView( model.getState() );

        // Listen for state changes so the representation can be updated.
        model.addListener( new GameModel2.GameModelListener() {
            public void stateChanged() {
                updateView( model.getState() );
            }
        } );

        // TODO: Temp - put a sketch of the tab up as a very early prototype.
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "tab-2-sketch-01.png" ));
        //        image.scale( 0.75 );
        //        image.setOffset( 50, 0 );
        //        rootNode.addChild(image);
    }

    private void updateView( GameModel2.State state ) {
        if ( state instanceof GameModel2.GameSettingsState ) {
            rootNode.removeChild( scoreboard );
            rootNode.removeChild( checkButton );
            gameSettingsNode.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
            rootNode.addChild( gameSettingsNode );
        }
        else if ( state instanceof GameModel2.PlayingGame ) {
            checkButton.setOffset( 600, 400 );
            rootNode.addChild( checkButton );
            scoreboard.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - scoreboard.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height - ( 1.3 * scoreboard.getFullBoundsReference().height ) );
            rootNode.addChild( scoreboard );
            rootNode.removeChild( gameSettingsNode );
        }
    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */

    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
