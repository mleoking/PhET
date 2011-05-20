// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.games;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Scoreboard for a game.
 * This is a generalization of the scoreboard used in reactants-products-and-leftovers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameScoreboardNode extends PhetPNode {

    // images
    private static final BufferedImage STOPWATCH_ICON = GamesResources.getImage( "blue-stopwatch.png" );

    // localized strings
    private static final String LABEL_SCORE = PhetCommonResources.getString( "Games.label.score" );
    private static final String BUTTON_NEW_GAME = PhetCommonResources.getString( "Games.button.newGame" );
    private static final String LABEL_LEVEL = PhetCommonResources.getString( "Games.label.level" );
    private static final String LABEL_BEST = PhetCommonResources.getString( "Games.label.best" );
    private static final String FORMAT_TIME_BEST = PhetCommonResources.getString( "Games.format.time.best" );
    private static final String MESSAGE_CONFIRM_NEW_GAME = PhetCommonResources.getString( "Games.message.confirmNewGame" );
    private static final String CONFIRM_TITLE = PhetCommonResources.getString( "Common.title.confirm" );

    // "look" properties
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BUTTON_COLOR = new Color( 235, 235, 235 );
    private static final int FONT_SIZE = 24;
    private static final PhetFont FONT = new PhetFont( FONT_SIZE );
    private static final PhetFont BUTTON_FONT = new PhetFont( Font.BOLD, 18 );

    // layout properties
    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 5;

    // immutable members
    private final NumberFormat pointsFormat;
    private final PText scoreNode, levelNode, timerValue;
    private final PImage timerIcon;
    private final ButtonNode newGameButton;
    private final PPath backgroundNode;
    private final Rectangle2D backgroundShape;
    private final EventListenerList listeners;

    // mutable members
    private boolean confirmNewGame; // request confirmation when "New Game" button is pressed?

    /**
     * Constructor
     *
     * @param maxLevel     the maximum level, used to adjust the layout to accommodate the maximum width
     * @param maxScore     the maximum score, used to adjust the layout to accommodate the maximum width
     * @param pointsFormat points are displayed in this format
     */
    public GameScoreboardNode( int maxLevel, double maxScore, NumberFormat pointsFormat ) {

        this.pointsFormat = pointsFormat;
        this.confirmNewGame = true;
        this.listeners = new EventListenerList();

        // Level
        levelNode = new PText();
        levelNode.setFont( FONT );
        setLevel( maxLevel ); // start with this, so we have a reasonable size for layout

        // Score
        scoreNode = new PText();
        scoreNode.setFont( FONT );
        setScore( maxScore ); // start with this, so we have a reasonable size for layout

        // timer
        timerIcon = new PImage( STOPWATCH_ICON );
        timerValue = new PText();
        timerValue.setFont( FONT );
        setTime( 0 ); // start with this, so we have a reasonable size for layout

        // New Game button
        newGameButton = new ButtonNode( BUTTON_NEW_GAME, BUTTON_FONT, BUTTON_COLOR );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleNewGame();
            }
        } );

        // rendering order
        addChild( levelNode );
        addChild( scoreNode );
        addChild( timerIcon );
        addChild( timerValue );
        addChild( newGameButton );

        // layout, everything in a row, vertically centered, offsets were set by eyeballing them
        final double maxChildHeight = getMaxChildHeight();
        // level
        double x = X_MARGIN;
        double y = Y_MARGIN + ( ( maxChildHeight - levelNode.getFullBoundsReference().getHeight() ) / 2 );
        levelNode.setOffset( x, y );
        // score
        x = levelNode.getFullBoundsReference().getMaxX() + 80;
        y = Y_MARGIN + ( ( maxChildHeight - scoreNode.getFullBoundsReference().getHeight() ) / 2 );
        scoreNode.setOffset( x, y );
        // timer
        x = scoreNode.getFullBoundsReference().getMaxX() + 60;
        y = Y_MARGIN + ( ( maxChildHeight - timerIcon.getFullBoundsReference().getHeight() ) / 2 );
        timerIcon.setOffset( x, y );
        x = timerIcon.getFullBoundsReference().getMaxX() + 5;
        y = Y_MARGIN + ( ( maxChildHeight - timerValue.getFullBoundsReference().getHeight() ) / 2 );
        timerValue.setOffset( x, y );
        // New Game button
        x = timerValue.getFullBoundsReference().getMaxX() + 20;
        y = Y_MARGIN + ( ( maxChildHeight - newGameButton.getFullBoundsReference().getHeight() ) / 2 );
        newGameButton.setOffset( x, y );

        // background, added last since it's sized to fit the child nodes above
        PBounds b = getFullBoundsReference();
        backgroundShape = new Rectangle2D.Double( 0, 0, b.getMaxX() + X_MARGIN, b.getMaxY() + Y_MARGIN );
        backgroundNode = new PPath( backgroundShape );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        addChild( backgroundNode );
        backgroundNode.moveToBack();
    }

    public void setBackground( Paint background ) {
        backgroundNode.setPaint( background );
    }

    /**
     * Changes the width of the scoreboard's background, while keeping the "New Game" button
     * at the right edge.  Used to adjust the scoreboard width to other stuff in the play area.
     *
     * @param width
     */
    public void setBackgroundWidth( double width ) {
        backgroundShape.setRect( backgroundShape.getX(), backgroundShape.getY(), width, backgroundShape.getHeight() );
        backgroundNode.setPathTo( backgroundShape );
        double x = backgroundNode.getFullBoundsReference().getMaxX() - newGameButton.getFullBoundsReference().getWidth() - X_MARGIN;
        newGameButton.setOffset( x, newGameButton.getYOffset() );
    }

    public void setScore( double points ) {
        String s = MessageFormat.format( LABEL_SCORE, pointsFormat.format( points ) );
        scoreNode.setText( s );
    }

    public void setLevel( int level ) {
        String s = MessageFormat.format( LABEL_LEVEL, String.valueOf( level ) );
        levelNode.setText( s );
    }

    public void setTimerVisible( boolean visible ) {
        timerIcon.setVisible( visible );
        timerValue.setVisible( visible );
    }

    /**
     * Sets the time without a "best time".
     *
     * @param time time in milliseconds
     */
    public void setTime( long time ) {
        setTime( time, 0 /* bestTime */ );
    }

    /**
     * Sets the time and a "best time" for comparison.
     * If you don't want to display a best time, use the other setTime method.
     *
     * @param time     milliseconds
     * @param bestTime best time in milliseconds, zero means "no best time"
     */
    public void setTime( long time, long bestTime ) {
        String valueString = null;
        if ( bestTime == 0 ) {
            // 0:29
            valueString = GameTimerFormat.format( time );
        }
        else {
            // (Best: 0:34)
            String bestTimeString = MessageFormat.format( LABEL_BEST, GameTimerFormat.format( bestTime ) );
            // 0:29 (Best: 0:34)
            valueString = MessageFormat.format( FORMAT_TIME_BEST, GameTimerFormat.format( time ), bestTimeString );
        }
        timerValue.setText( valueString );
    }

    public void setConfirmNewGame( boolean confirmNewGame ) {
        this.confirmNewGame = confirmNewGame;
    }

    public boolean isConfirmNewGame() {
        return confirmNewGame;
    }

    /*
    * Handle the "New Game" button.
    * If confirmation is enabled, posts a confirmation dialog.
    * When confirmed, listeners are notified.
    */
    private void handleNewGame() {
        if ( confirmNewGame ) {
            // request confirmation via a Yes/No dialog
            Component parent = PhetApplication.getInstance().getPhetFrame();
            int option = PhetOptionPane.showYesNoDialog( parent, MESSAGE_CONFIRM_NEW_GAME, CONFIRM_TITLE );
            if ( option == JOptionPane.YES_OPTION ) {
                fireNewGamePressed();
            }
        }
        else {
            fireNewGamePressed();
        }
    }

    // max height of this nodes children, used for layout alignment
    private double getMaxChildHeight() {
        double maxHeight = 0;
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            maxHeight = Math.max( maxHeight, getChild( i ).getFullBoundsReference().getHeight() );
        }
        return maxHeight;
    }

    public interface GameScoreboardListener extends EventListener {
        public void newGamePressed();
    }

    public void addGameScoreboardListener( GameScoreboardListener listener ) {
        listeners.add( GameScoreboardListener.class, listener );
    }

    public void removeGameScoreboardListener( GameScoreboardListener listener ) {
        listeners.remove( GameScoreboardListener.class, listener );
    }

    private void fireNewGamePressed() {
        for ( GameScoreboardListener listener : listeners.getListeners( GameScoreboardListener.class ) ) {
            listener.newGamePressed();
        }
    }
}