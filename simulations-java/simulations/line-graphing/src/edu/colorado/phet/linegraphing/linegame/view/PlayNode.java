// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameScoreboardNode.GameScoreboardListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;

/**
 * Portion of the scenegraph that corresponds to the "play" game phase
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PlayNode extends PhetPNode {

    public PlayNode( final LineGameModel model, Dimension2D stageSize ) {

        final GameScoreboardNode scoreboardNode = new GameScoreboardNode( model.settings.level.getMax(), model.getPerfectScore(), new DecimalFormat( "0" ) );
        scoreboardNode.setBackgroundWidth( stageSize.getWidth() - 150 );
        addChild( scoreboardNode );

        // bottom center
        scoreboardNode.setOffset( ( stageSize.getWidth() - scoreboardNode.getFullBoundsReference().getWidth() ) / 2,
                                  stageSize.getHeight() - scoreboardNode.getFullBoundsReference().getHeight() - 10 );

        // When "New Game" button is pressed, change game phase
        scoreboardNode.addGameScoreboardListener( new GameScoreboardListener() {
            public void newGamePressed() {
                model.phase.set( GamePhase.SETTINGS );
            }
        } );

        // points on the scoreboard
        model.score.addObserver( new SimpleObserver() {
            public void update() {
                scoreboardNode.setScore( model.score.get() );
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
                scoreboardNode.setTime( t, model.getBestTime( model.settings.level.get() ) );
            }
        } );

        //XXX temporary test buttons for ending game
        {
            // end game with perfect score
            TextButtonNode endWithPerfectScoreButton = new TextButtonNode( "End with perfect score", new PhetFont( 30 ), Color.GREEN );
            endWithPerfectScoreButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.score.set( model.getPerfectScore() );
                    model.phase.set( GamePhase.RESULTS );
                }
            } );
            addChild( endWithPerfectScoreButton );
            endWithPerfectScoreButton.setOffset( ( stageSize.getWidth() - endWithPerfectScoreButton.getFullBoundsReference().getWidth() ) / 2, 100 );

            // end game with imperfect score
            TextButtonNode endWithImperfectScoreButton = new TextButtonNode( "End with imperfect score", new PhetFont( 30 ), Color.RED );
            endWithImperfectScoreButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.score.set( model.getPerfectScore() - 1 );
                    model.phase.set( GamePhase.RESULTS );
                }
            } );
            addChild( endWithImperfectScoreButton );
            endWithImperfectScoreButton.setOffset( ( stageSize.getWidth() - endWithImperfectScoreButton.getFullBoundsReference().getWidth() ) / 2,
                                                   endWithPerfectScoreButton.getFullBoundsReference().getMaxY() + 25 );
        }
    }
}
