/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.GameModel;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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

    private final ArrayList<StateView> stateViews = new ArrayList<StateView>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final GameModel model ) {

        this.model = model;
        stateViews.add( new GameSettingsStateView( this ) );
        stateViews.add( new GameOverStateView( this ) );

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

            public void problemSetCreated( GameModel.ProblemSet problemSet ) {
                //create views for the problem set
                for ( int i = 0; i < problemSet.getNumCompleteTheModelProblems(); i++ ) {
                    final GameModel.CompleteTheModelProblem problem = problemSet.getCompleteTheModelProblem( i );
                    stateViews.add( new CompleteTheModelProblemView( GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
                for ( int i = 0; i < problemSet.getNumCompleteTheSymbolProblems(); i++ ) {
                    final GameModel.CompleteTheSymbolProblem problem = problemSet.getCompleteTheSymbolProblem( i );
                    stateViews.add( new CompleteTheSymbolProblemView( GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
                for ( int i = 0; i < problemSet.getNumHowManyParticlesProblems(); i++ ) {
                    final GameModel.HowManyParticlesProblem problem = problemSet.getHowManyParticlesProblem( i );
                    stateViews.add( new HowManyParticlesProblemView( GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
            }
        } );
        getView( model.getState() ).init();
    }

    private StateView getView( GameModel.State state ) {
        for ( StateView stateView : stateViews ) {
            if ( stateView.getState() == state ) {
                return stateView;
            }
        }
        throw new RuntimeException( "No state found for state:" + state );
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

    private static abstract class StateView {
        private final GameCanvas gameCanvas;
        GameModel.State state;

        protected StateView( GameCanvas gameCanvas, GameModel.State state ) {
            this.gameCanvas = gameCanvas;
            this.state = state;
        }

        public GameModel.State getState() {
            return state;
        }

        public abstract void teardown();

        public abstract void init();

        public GameScoreboardNode getScoreboard() {
            return gameCanvas.scoreboard;
        }

        public void addChild( PNode child ) {
            gameCanvas.rootNode.addChild( child );
        }

        public void removeChild( PNode child ) {
            gameCanvas.rootNode.removeChild( child );
        }
    }

    private class GameOverStateView extends StateView {
        private final GameOverNode gameOverNode;

        private GameOverStateView( GameCanvas gameCanvas ) {
            super( gameCanvas, model.getGameOverState() );
            gameOverNode = new GameOverNode( 1, 5, 5, new DecimalFormat( "0.#" ), 40000, 30000, false, true );
            gameOverNode.addGameOverListener( new GameOverNode.GameOverListener() {
                public void newGamePressed() {
                    model.newGame();
                }
            } );
        }

        public void init() {
            gameOverNode.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameOverNode.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameOverNode.getFullBoundsReference().height / 2 );
            rootNode.addChild( gameOverNode );
        }

        public void teardown() {
            rootNode.removeChild( gameOverNode );
        }
    }

    private class GameSettingsStateView extends StateView {
        private final GameSettingsPanel panel;
        private final PNode gameSettingsNode;

        private GameSettingsStateView( GameCanvas gameCanvas ) {
            super( gameCanvas, model.getGameSettingsState() );
            panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
            gameSettingsNode = new PSwing( panel );
            panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
                @Override
                public void startButtonPressed() {
                    model.startGame( panel.getLevel(), panel.isTimerOn(), panel.isSoundOn() );
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

    private static class ProblemView extends StateView {

        // TODO: i18n
        private final GradientButtonNode checkButton = new GradientButtonNode( "Check", BUTTONS_FONT_SIZE, BUTTONS_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    problem.checkGuess();
                }
            } );
        }};
        private final PText problemNumberDisplay;

        GameModel.Problem problem;

        private ProblemView( GameCanvas gameCanvas, GameModel.Problem problem, int problemIndex, int totalNumProblems ) {
            super( gameCanvas, problem );
            this.problem = problem;
            problemNumberDisplay = new PText( "Problem " + problemIndex + " of " + totalNumProblems ) {{
                setFont( new PhetFont( 20, true ) );
            }};
            problemNumberDisplay.setOffset( 30, 30 );
        }

        public void init() {
            checkButton.setOffset( 700, 500 );
            getScoreboard().setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - getScoreboard().getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height - ( 1.3 * getScoreboard().getFullBoundsReference().height ) );
            addChild( checkButton );
            addChild( getScoreboard() );
            addChild( problemNumberDisplay );
        }

        public void teardown() {
            removeChild( getScoreboard() );
            removeChild( checkButton );
            removeChild( problemNumberDisplay );
        }
    }

    private static class CompleteTheModelProblemView extends ProblemView {
        private PText description = new PText( "Complete the model:" ) {{
            setFont( new PhetFont( 20, true ) );
            setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
        }};
        private SymbolIndicatorNode symbolIndicatorNode;

        public CompleteTheModelProblemView( GameCanvas canvas, GameModel.CompleteTheModelProblem problem, int problemIndex, int totalNumProblems ) {
            super( canvas, problem, problemIndex, totalNumProblems );
            symbolIndicatorNode = new SymbolIndicatorNode( problem.getAtom() );
            symbolIndicatorNode.scale( 2 );
            symbolIndicatorNode.setOffset( 100, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );
        }

        @Override
        public void init() {
            super.init();
            addChild( description );
            addChild( symbolIndicatorNode );
        }

        @Override
        public void teardown() {
            super.teardown();
            removeChild( description );
            removeChild( symbolIndicatorNode );
        }
    }

    private static class CompleteTheSymbolProblemView extends ProblemView {
        private PText description = new PText( "Complete the symbol:" ) {{
            setFont( new PhetFont( 20, true ) );
            setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
        }};

        public CompleteTheSymbolProblemView( GameCanvas canvas, GameModel.CompleteTheSymbolProblem completeTheSymbolProblem, int problemIndex, int totalNumProblems ) {
            super( canvas, completeTheSymbolProblem, problemIndex, totalNumProblems );
        }

        @Override
        public void init() {
            super.init();
            addChild( description );
        }

        @Override
        public void teardown() {
            super.teardown();
            removeChild( description );
        }

    }

    private static class HowManyParticlesProblemView extends ProblemView {
        private PText description = new PText( "How many particles?" ) {{
            setFont( new PhetFont( 20, true ) );
            setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
        }};

        public HowManyParticlesProblemView( GameCanvas canvas, GameModel.HowManyParticlesProblem howManyParticlesProblem, int problemIndex, int totalNumProblems ) {
            super( canvas, howManyParticlesProblem, problemIndex, totalNumProblems );

        }

        @Override
        public void init() {
            super.init();
            addChild( description );
        }

        @Override
        public void teardown() {
            super.teardown();
            removeChild( description );
        }

    }
}
