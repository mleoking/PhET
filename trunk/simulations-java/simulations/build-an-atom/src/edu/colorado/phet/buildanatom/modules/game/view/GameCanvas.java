/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheModelProblem;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheSymbolProblem;
import edu.colorado.phet.buildanatom.modules.game.model.HowManyParticlesProblem;
import edu.colorado.phet.buildanatom.modules.game.model.ProblemSet;
import edu.colorado.phet.buildanatom.modules.game.model.State;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the game tab.
 */
public class GameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final int BUTTONS_FONT_SIZE = 30;
    public static final Color BUTTONS_COLOR = new Color( 255, 255, 0, 150 ); // translucent yellow

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private final BuildAnAtomGameModel model;

    // View
    private final PNode rootNode;

    private final GameScoreboardNode scoreboard = new GameScoreboardNode( BuildAnAtomGameModel.MAX_LEVELS, BuildAnAtomGameModel.MAX_SCORE, new DecimalFormat( "0.#" ) ) {
        {
            setBackgroundWidth( BuildAnAtomDefaults.STAGE_SIZE.width * 0.85 );
        }
    };

    private final ArrayList<StateView> stateViews = new ArrayList<StateView>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final BuildAnAtomGameModel model ) {

        this.model = model;
        stateViews.add( new GameSettingsStateView( this,model ) );
        stateViews.add( new GameOverStateView( this, model ) );

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
        model.addListener( new BuildAnAtomGameModel.GameModelListener() {
            public void stateChanged( State oldState, State newState ) {
                getView( oldState ).teardown();
                getView( newState ).init();
            }

            public void problemSetCreated( ProblemSet problemSet ) {
                //create views for the problem set
                for ( int i = 0; i < problemSet.getNumCompleteTheModelProblems(); i++ ) {
                    final CompleteTheModelProblem problem = problemSet.getCompleteTheModelProblem( i );
                    stateViews.add( new CompleteTheModelProblemView(  model, GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
                for ( int i = 0; i < problemSet.getNumCompleteTheSymbolProblems(); i++ ) {
                    final CompleteTheSymbolProblem problem = problemSet.getCompleteTheSymbolProblem( i );
                    stateViews.add( new CompleteTheSymbolProblemView( model, GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
                for ( int i = 0; i < problemSet.getNumHowManyParticlesProblems(); i++ ) {
                    final HowManyParticlesProblem problem = problemSet.getHowManyParticlesProblem( i );
                    stateViews.add( new HowManyParticlesProblemView( model, GameCanvas.this, problem, problemSet.getProblemIndex( problem ) + 1, problemSet.getTotalNumProblems() ) );
                }
            }
        } );
        getView( model.getState() ).init();
    }

    private StateView getView( State state ) {
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

    public GameScoreboardNode getScoreboard() {
        return scoreboard;
    }

    public PNode getRootNode() {
        return rootNode;
    }

}
