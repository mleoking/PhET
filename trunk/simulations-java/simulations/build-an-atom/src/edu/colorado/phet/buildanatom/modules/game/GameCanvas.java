/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
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
    private final GameModel model;

    // View
    private final PNode rootNode;

    private final GameScoreboardNode scoreboard = new GameScoreboardNode( GameModel.MAX_LEVELS, GameModel.MAX_SCORE, new DecimalFormat( "0.#" ) ) {
        {
            setBackgroundWidth( BuildAnAtomDefaults.STAGE_SIZE.width * 0.85 );
        }
    };
    private final StateView gameSettingsStateView = new GameSettingsStateView();
    private final StateView challengeStateView = new ChallengeStateView();
    private final StateView gameOverStateView = new GameOverStateView();
    private final StateView playingGameStateView2 = new ChallengeStateView() {
        final PText child = new PText( "Level 2" );

        @Override
        public GameModel.State getState() {
            return model.getLevel2();
        }

        @Override
        public void teardown() {
            super.teardown();
            rootNode.removeChild( child );
        }

        @Override
        public void init() {
            super.init();
            rootNode.addChild( child );
        }
    };

    private final ArrayList<StateView> stateViews = new ArrayList<StateView>() {{
        add( gameSettingsStateView );
        add( challengeStateView );
        add( playingGameStateView2 );
        add( gameOverStateView );
    }};

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final GameModel model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Background.
        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Set up listener for the button on the score board that indicates that
        // a new game is desired.
        scoreboard.addGameScoreboardListener( new GameScoreboardNode.GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // TODO: Temp - Put a "TBD" indicator on the canvas.
        //        PText tbdIndicator = new PText( "TBD" );
        //        tbdIndicator.setFont( new PhetFont( 100, true ) );
        //        tbdIndicator.setTextPaint( new Color(0, 0, 0, 100) );
        //        tbdIndicator.setOffset( 380, 50 );
        //        rootNode.addChild( tbdIndicator );

        // Listen for state changes so the representation can be updated.
        model.addListener( new GameModel.GameModelListener() {
            public void stateChanged( GameModel.State oldState, GameModel.State newState ) {
                getView( oldState ).teardown();
                getView( newState ).init();
            }
        } );
        getView( model.getState() ).init();

        // TODO: Temp - put a sketch of the tab up as a very early prototype.
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "tab-2-sketch-01.png" ));
        //        image.scale( 0.75 );
        //        image.setOffset( 50, 0 );
        //        rootNode.addChild(image);
    }

    private StateView getView( GameModel.State state ) {
        for ( StateView stateView : stateViews ) {
            if ( stateView.getState() == state ) {
                return stateView;
            }
        }
        throw new RuntimeException( "No state found" );
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

    private abstract class StateView {
        GameModel.State state;

        protected StateView( GameModel.State state ) {
            this.state = state;
        }

        public GameModel.State getState() {
            return state;
        }

        public abstract void teardown();

        public abstract void init();
    }

    private class GameOverStateView extends StateView {
        private final GameOverNode gameOverNode;

        private GameOverStateView() {
            super( model.getGameOverState() );
            gameOverNode = new GameOverNode( 1, 5, 5, new DecimalFormat( "0.#" ), 40000, 30000, false, true );
            gameOverNode.addGameOverListener( new GameOverNode.GameOverListener() {
                public void newGamePressed() {
                    model.newGame();
                }
            } );
        }

        public void teardown() {
            rootNode.removeChild( gameOverNode );
        }

        public void init() {
            gameOverNode.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameOverNode.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameOverNode.getFullBoundsReference().height / 2 );
            rootNode.addChild( gameOverNode );
        }
    }

    private class GameSettingsStateView extends StateView {
        private final GameSettingsPanel panel;
        private final PNode gameSettingsNode;

        private GameSettingsStateView() {
            super( model.getGameSettingsState() );
            panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
            gameSettingsNode = new PSwing( panel );
            panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
                @Override
                public void startButtonPressed() {
                    model.startGame();
                }
            } );
        }

        public void teardown() {
            rootNode.removeChild( gameSettingsNode );
        }

        public void init() {
            gameSettingsNode.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
            rootNode.addChild( gameSettingsNode );
        }
    }

    private class ChallengeStateView extends StateView {

        // TODO: i18n
        private final GradientButtonNode checkButton = new GradientButtonNode( "Check", BUTTONS_FONT_SIZE, BUTTONS_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getPlayingGameState().checkGuess();
                }
            } );
        }};

        protected ChallengeStateView() {
            super( model.getPlayingGameState() );
        }

        public GameModel.State getState() {
            return model.getPlayingGameState();
        }

        public void teardown() {
            rootNode.removeChild( scoreboard );
            rootNode.removeChild( checkButton );
        }

        public void init() {
            checkButton.setOffset( 700, 500 );
            scoreboard.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - scoreboard.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height - ( 1.3 * scoreboard.getFullBoundsReference().height ) );
            rootNode.addChild( checkButton );
            rootNode.addChild( scoreboard );
        }
    }
}
