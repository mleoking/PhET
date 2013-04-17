// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameScoreboardNode.GameScoreboardListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.GamePhase;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Portion of the scenegraph that corresponds to the "play" game phase. (See GamePhase.PLAY)
 * Displays the scoreboard and current challenge.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PlayNode extends PhetPNode {

    public PlayNode( final LineGameModel model, Dimension2D stageSize, final GameAudioPlayer audioPlayer ) {

        final GameScoreboardNode scoreboardNode = new GameScoreboardNode( model.settings.level.getMax(), model.getPerfectScore(), new DecimalFormat( "0" ) );
        scoreboardNode.setBackgroundWidth( stageSize.getWidth() - 150 ); // bottom center
        scoreboardNode.setOffset( ( stageSize.getWidth() - scoreboardNode.getFullBoundsReference().getWidth() ) / 2,
                                  stageSize.getHeight() - scoreboardNode.getFullBoundsReference().getHeight() - 10 );
        addChild( scoreboardNode );

        // compute the size of the area available for the challenges
        final PDimension challengeSize = new PDimension( stageSize.getWidth(), scoreboardNode.getFullBoundsReference().getMinY() );

        // challenge parent, to maintain rendering order
        final PNode challengeParent = new PNode();
        addChild( challengeParent );

        // When "New Game" button is pressed, change game phase
        scoreboardNode.addGameScoreboardListener( new GameScoreboardListener() {
            public void newGamePressed() {
                model.phase.set( GamePhase.SETTINGS );
            }
        } );

        // level on the scoreboard
        model.settings.level.addObserver( new SimpleObserver() {
            public void update() {
                scoreboardNode.setLevel( model.settings.level.get() );
            }
        } );

        // points on the scoreboard
        model.results.score.addObserver( new SimpleObserver() {
            public void update() {
                scoreboardNode.setScore( model.results.score.get() );
            }
        } );

        // timer visibility on the scoreboard
        model.settings.timerEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                scoreboardNode.setTimerVisible( enabled );
            }
        } );

        // timer
        model.timer.time.addObserver( new VoidFunction1<Long>() {
            public void apply( Long t ) {
                scoreboardNode.setTime( t, model.results.getBestTime( model.settings.level.get() ) );
            }
        } );

        // Set up a new challenge
        model.challenge.addObserver( new VoidFunction1<Challenge>() {
            public void apply( Challenge challenge ) {
                challengeParent.removeAllChildren();
                challengeParent.addChild( challenge.createView( model, challengeSize, audioPlayer ) );
            }
        } );
    }
}
