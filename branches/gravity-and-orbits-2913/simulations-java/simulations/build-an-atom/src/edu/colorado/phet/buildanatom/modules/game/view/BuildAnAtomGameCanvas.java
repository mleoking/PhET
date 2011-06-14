// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.*;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the game tab.
 */
public class BuildAnAtomGameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final int BUTTONS_FONT_SIZE = 30;
    public static final Color BUTTONS_COLOR = new Color( 255, 255, 0, 150 ); // translucent yellow

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    private final GameScoreboardNode scoreboard;
    private StateView currentView;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomGameCanvas( final BuildAnAtomGameModel model ) {

        scoreboard = new GameScoreboardNode( BuildAnAtomGameModel.MAX_LEVELS, model.getMaximumPossibleScore(), new DecimalFormat( "0.#" ) ) {{
            setBackgroundWidth( BuildAnAtomDefaults.STAGE_SIZE.width * 0.85 );
            model.getGameClock().addClockListener( new ClockAdapter() {
                @Override
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    if ( model.isTimerEnabled() && model.isBestTimeRecorded( model.getLevel() ) ) {
                        setTime( model.getTime(), model.getBestTime( model.getLevel() ) );
                    }
                    else {
                        setTime( model.getTime() );
                    }
                }
            } );
            model.getScoreProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setScore( model.getScoreProperty().get() );
                }
            } );
            model.getGameSettings().timerEnabled.addObserver( new SimpleObserver() {
                public void update() {
                    setTimerVisible( model.getGameSettings().timerEnabled.get() );
                }
            } );
            model.getGameSettings().level.addObserver( new SimpleObserver() {
                public void update() {
                    setLevel( model.getGameSettings().level.get() );
                }
            } );
        }};

        // Set up listener for the button on the score board that indicates that
        // a new game is desired.
        scoreboard.addGameScoreboardListener( new GameScoreboardNode.GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Background.
        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

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
        return state.createView( this );
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
