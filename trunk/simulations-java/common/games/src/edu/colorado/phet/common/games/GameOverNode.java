// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.games;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.games.GameSimSharing.ModelActions;
import edu.colorado.phet.common.games.GameSimSharing.ModelComponents;
import edu.colorado.phet.common.games.GameSimSharing.ParameterKeys;
import edu.colorado.phet.common.games.GameSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Upon completion of a Game, this node is used to display a summary of the user's game results.
 * <p/>
 * This is a generalization of the "Game Over" node from reactants-products-and-leftovers.
 * <p/>
 * Note that this was originally implemented using a JPanel, wrapped with PSwing.
 * But some problems with PSwing bounds (see #2219) forced a rewrite.
 * Layout is now done using node offsets, PText is used instead of JLabels,
 * and the background is a PPath instead of a JPanel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameOverNode extends PhetPNode {

    // localized strings
    private static final String TITLE_GAME_OVER = PhetCommonResources.getString( "Games.title.gameOver" );
    private static final String BUTTON_NEW_GAME = PhetCommonResources.getString( "Games.button.newGame" );
    private static final String LABEL_LEVEL = PhetCommonResources.getString( "Games.label.level" );
    private static final String LABEL_SCORE_IMPERFECT = PhetCommonResources.getString( "Games.label.score.max" );
    private static final String LABEL_SCORE_PERFECT = PhetCommonResources.getString( "Games.label.score.max.perfect" );
    private static final String LABEL_TIME = PhetCommonResources.getString( "Games.label.time" );
    private static final String LABEL_BEST = PhetCommonResources.getString( "Games.label.best" );
    private static final String LABEL_NEW_BEST = PhetCommonResources.getString( "Games.label.newBest" );
    private static final String FORMAT_TIME_BEST = PhetCommonResources.getString( "Games.format.time.best" );

    // "look" properties
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final Font LABEL_FONT = new PhetFont( 18 );
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );

    // layout properties
    private static final double MIN_SEPARATOR_WIDTH = 175;
    private static final double X_MARGIN = 25;
    private static final double Y_MARGIN = 15;
    private static final double Y_SPACING = 15;

    private final NumberFormat scoreFormat;
    private final EventListenerList listeners;

    /**
     * Constructor.
     *
     * @param level         level number
     * @param score         user's score on the game that just ended
     * @param perfectScore  perfect (highest possible) score on the game that just ended
     * @param scoreFormat   how the scores (user's and perfect) should be formatted
     * @param time          user's time on the game that just ended
     * @param bestTime      the user's best time on all games played so far (typically for a specific level)
     * @param isNewBestTime is the user's time a new "best" time? If so, this identifies the time as "new best".
     * @param timerVisible  was the timer visible during the game that just ended? Time is only shown if the timer was used during game play.
     */
    public GameOverNode( int level, double score, double perfectScore, NumberFormat scoreFormat, long time, long bestTime, boolean isNewBestTime, boolean timerVisible ) {
        super();

        /*
         * Report on the game completed, assumes that this node is only created when the game is completed
         * (a safe assumption because the constructor args are only available at end of game)
         */
        SimSharingManager.sendModelMessage( ModelComponents.game, ModelComponentTypes.feature, ModelActions.completed,
                                            ParameterSet.parameterSet( ParameterKeys.level, level ).
                                                    with( ParameterKeys.score, score ).
                                                    with( ParameterKeys.perfectScore, perfectScore ).
                                                    with( ParameterKeys.time, time ).
                                                    with( ParameterKeys.bestTime, bestTime ).
                                                    with( ParameterKeys.isNewBestTime, isNewBestTime ).
                                                    with( ParameterKeys.timerVisible, timerVisible ) );

        this.scoreFormat = scoreFormat;
        this.listeners = new EventListenerList();

        // title
        PText titleNode = new PText( TITLE_GAME_OVER );
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );

        // level
        PText levelNode = new PText( getLevelString( level ) );
        levelNode.setFont( LABEL_FONT );
        addChild( levelNode );

        // score
        PText scoreNode = new PText( getScoreString( score, perfectScore ) );
        scoreNode.setFont( LABEL_FONT );
        addChild( scoreNode );

        // time
        boolean isPerfectScore = ( score == perfectScore );
        PText timeNode = new PText( getTimeString( time, bestTime, timerVisible, isPerfectScore, isNewBestTime ) );
        timeNode.setFont( LABEL_FONT );
        addChild( timeNode );

        // buttons
        HTMLImageButtonNode buttonNode = new HTMLImageButtonNode( BUTTON_NEW_GAME, GameSettingsPanel.DEFAULT_BUTTON_COLOR ) {{
            setUserComponent( UserComponents.newGameButton );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    fireNewGamePressed();
                }
            } );
        }};
        addChild( buttonNode );

        // horizontal separators, compute width after adding other children
        final double separatorWidth = Math.max( PNodeLayoutUtils.getMaxFullWidthChildren( this ), MIN_SEPARATOR_WIDTH );
        PPath separatorNode1 = new PPath( new Line2D.Double( 0, 0, separatorWidth, 0 ) );
        addChild( separatorNode1 );
        PPath separatorNode2 = new PPath( new Line2D.Double( 0, 0, separatorWidth, 0 ) );
        addChild( separatorNode2 );

        // create the background, after adding all other stuff
        final double backgroundWidth = separatorWidth + ( 2 * X_MARGIN );
        final double backgroundHeight = PNodeLayoutUtils.sumFullHeightsChildren( this ) + ( Y_SPACING * ( getChildrenCount() - 1 ) ) + ( 2 * Y_MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight ) );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        addChild( backgroundNode );
        backgroundNode.moveToBack();

        // layout
        double x = 0;
        double y = 0;
        backgroundNode.setOffset( x, y );
        // title centered at top
        x = ( backgroundNode.getFullBoundsReference().getWidth() - titleNode.getFullBoundsReference().getWidth() ) / 2;
        y = backgroundNode.getFullBoundsReference().getMinY() + Y_MARGIN;
        titleNode.setOffset( x, y );
        // separator centered below title
        x = ( backgroundNode.getFullBoundsReference().getWidth() - separatorNode1.getFullBoundsReference().getWidth() ) / 2;
        y = titleNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        separatorNode1.setOffset( x, y );
        // level, score and time left justified below separator
        y = separatorNode1.getFullBoundsReference().getMaxY() + Y_SPACING;
        levelNode.setOffset( x, y );
        y = levelNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        scoreNode.setOffset( x, y );
        y = scoreNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        timeNode.setOffset( x, y );
        // separator centered below level, score and time
        y = timeNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        separatorNode2.setOffset( x, y );
        // button centered below separator
        x = ( backgroundNode.getFullBoundsReference().getWidth() - buttonNode.getFullBoundsReference().getWidth() ) / 2;
        y = separatorNode2.getFullBoundsReference().getMaxY() + Y_SPACING;
        buttonNode.setOffset( x, y );
    }

    /*
    * Gets the level string.
    */
    private static String getLevelString( int level ) {
        return MessageFormat.format( LABEL_LEVEL, String.valueOf( level ) );
    }

    /*
    * Gets the score string.
    * If we had a perfect score, indicate that.
    */
    private String getScoreString( double score, double perfectScore ) {
        String pointsString = scoreFormat.format( score );
        String perfectScoreString = scoreFormat.format( perfectScore );
        String scoreString = null;
        if ( score == perfectScore ) {
            scoreString = MessageFormat.format( LABEL_SCORE_PERFECT, pointsString, perfectScoreString );
        }
        else {
            scoreString = MessageFormat.format( LABEL_SCORE_IMPERFECT, pointsString, perfectScoreString );
        }
        return scoreString;
    }

    /*
    * Gets the time string.
    * If we had an imperfect score, simply show the time.
    * If we had a perfect score, show the best time, and indicate if the time was a "new best".
    */
    private static String getTimeString( long time, long bestTime, boolean timerVisible, boolean isPerfectScore, boolean isNewBestTime ) {
        String s = " ";
        if ( timerVisible ) {
            // Time: 0:29
            String timeString = MessageFormat.format( LABEL_TIME, GameTimerFormat.format( time ) );
            if ( !isPerfectScore ) {
                // Time: 0:29
                s = timeString;
            }
            else if ( isNewBestTime ) {
                // Time: 0:29 (NEW BEST!)
                s = MessageFormat.format( FORMAT_TIME_BEST, timeString, LABEL_NEW_BEST );
            }
            else {
                // (Best: 0:20)
                String bestTimeString = MessageFormat.format( LABEL_BEST, GameTimerFormat.format( bestTime ) );
                // Time: 0:29 (Best: 0:20)
                s = MessageFormat.format( FORMAT_TIME_BEST, timeString, bestTimeString );
            }
        }
        return s;
    }

    public interface GameOverListener extends EventListener {
        public void newGamePressed();
    }

    public void addGameOverListener( GameOverListener listener ) {
        listeners.add( GameOverListener.class, listener );
    }

    public void removeGameOverListener( GameOverListener listener ) {
        listeners.remove( GameOverListener.class, listener );
    }

    private void fireNewGamePressed() {
        for ( GameOverListener listener : listeners.getListeners( GameOverListener.class ) ) {
            listener.newGamePressed();
        }
    }
}
