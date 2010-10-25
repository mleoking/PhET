/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheModelProblem;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheSymbolProblem;
import edu.colorado.phet.buildanatom.modules.game.model.HowManyParticlesProblem;
import edu.colorado.phet.buildanatom.modules.game.model.ProblemSet;
import edu.colorado.phet.buildanatom.modules.game.model.State;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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

    private final GameScoreboardNode scoreboard;
    private StateView currentView;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final BuildAnAtomGameModel model ) {

        this.model = model;
        scoreboard= new GameScoreboardNode( BuildAnAtomGameModel.MAX_LEVELS, model.getMaximumPossibleScore(), new DecimalFormat( "0.#" ) ) {{
            setBackgroundWidth( BuildAnAtomDefaults.STAGE_SIZE.width * 0.85 );
            setScore( 0 );//todo: could this be moved to the bottom of GameScoreboardNode?
            setLevel( 1 );//todo: could this be moved to the bottom of GameScoreboardNode?
        }};
        this.model.getScoreProperty().addObserver( new SimpleObserver() {
            public void update() {
                scoreboard.setScore( model.getScoreProperty().getValue() );
            }
        } );

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

        // Listen for state changes so the representation can be updated.
        model.addListener( new BuildAnAtomGameModel.GameModelListener() {
            public void stateChanged( State oldState, State newState ) {
                currentView.teardown();
                currentView = createView(newState);
                currentView.init();
            }
        } );
        currentView = createView(model.getState() );
        currentView.init();
    }

        
        public StateView createView( State state ) {
            return state.createView(this);
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
