/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.util.TimeUtils;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Scoreboard, displays the current state of the Game.
 * Also has a "New Game" button that allows the user to start over at any time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScoreboardNode extends PhetPNode {
    
    // constants
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BUTTON_COLOR = new Color( 235, 235, 235 );
    private static final int BUTTON_FONT_SIZE = 18;
    private static final int FONT_SIZE = 24;
    private static final PhetFont FONT = new PhetFont( FONT_SIZE );
    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 5;
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    
    // immutable members
    private final GameModel model;
    private final PText scoreNode, levelNode, timerValue;
    private final PImage timerIcon;
    private final PPath backgroundNode;
    private final GradientButtonNode newGameButton;
    private final Rectangle2D backgroundShape;
    
    // mutable members
    private boolean confirmNewGame; // request confirmation when "New Game" button is pressed?
 
    public ScoreboardNode( final GameModel model ) {
        super();
        
        confirmNewGame = false;
        this.model = model;
        
        // Level
        levelNode = new PText();
        levelNode.setFont( FONT );
        setLevel( GameModel.getLevelRange().getMax() ); // start with this, so we have a reasonable size for layout
        
        // Score
        scoreNode = new PText(); 
        scoreNode.setFont( FONT );
        setScore( GameModel.getPerfectScore() ); // start with this, so we have a reasonable size for layout
        
        // timer
        timerIcon = new PImage( RPALImages.STOPWATCH );
        timerValue = new PText();
        timerValue.setFont( FONT );
        setTime( 0 ); // start with this, so we have a reasonable size for layout
        
        // New Game button
        newGameButton = new GradientButtonNode( RPALStrings.BUTTON_NEW_GAME, BUTTON_FONT_SIZE, BUTTON_COLOR );
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
        
        // listen to model
        model.addGameListener( new GameAdapter() {

            @Override
            public void levelChanged() {
                setLevel( model.getLevel() );
            }

            @Override
            public void pointsChanged() {
                setScore( model.getPoints() );
            }

            @Override
            public void timerVisibleChanged() {
                setTimerVisible( model.isTimerVisible() );
                if ( model.isTimerVisible() ) {
                    setTime( model.getTime() );
                }
            }
            
            @Override
            public void timeChanged() {
                setTime( model.getTime() );
            }
            
            @Override
            public void gameStarted() {
                confirmNewGame = true;
            }
            
            @Override 
            public void gameCompleted() {
                confirmNewGame = false;
            }
        });
        
        // initial state
        setScore( model.getPoints() );
        setLevel( model.getLevel() );
        setTimerVisible( model.isTimerVisible() );
        setTime( model.getTime() );
    }
    
    private void setScore( double points ) {
        String s = MessageFormat.format( RPALStrings.LABEL_SCORE, POINTS_FORMAT.format( points ) );
        scoreNode.setText( s );
    }
    
    private void setLevel( int level ) {
        String s = MessageFormat.format( RPALStrings.LABEL_LEVEL, String.valueOf( level ) );
        levelNode.setText( s );
    }
    
    private void setTimerVisible( boolean visible ) {
        timerIcon.setVisible( visible );
        timerValue.setVisible( visible );
    }
    
    private void setTime( long elapsedMillis ) {
        String valueString = null;
        long bestTime = model.getBestTime();
        if ( bestTime == GameModel.NO_TIME ) {
            // 0:29
            valueString = TimeUtils.getTimeString( elapsedMillis );
        }
        else {
            // (Best: 0:34)
            String bestTimeString = MessageFormat.format( RPALStrings.LABEL_BEST, TimeUtils.getTimeString( bestTime ) );
            // 0:29 (Best: 0:34)
            valueString = MessageFormat.format( RPALStrings.FORMAT_TIME_BEST, TimeUtils.getTimeString( elapsedMillis ), bestTimeString );
        }
        timerValue.setText( valueString );
    }
    
    private void handleNewGame() {
        if ( confirmNewGame ) {
            // request confirmation via a Yes/No dialog
            Component parent = PhetApplication.getInstance().getPhetFrame();
            String message = RPALStrings.MESSAGE_CONFIRM_NEW_GAME;
            String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
            int option = PhetOptionPane.showYesNoDialog( parent, message, title );
            if ( option == JOptionPane.YES_OPTION ) {
                model.newGame();
            }
        }
        else {
            model.newGame();
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
    
    /**
     * Changes the width of the scoreboard's background, while keeping the "New Game" button
     * at the right edge.  Used to adjust the scoreboard width to other stuff in the play area.
     * @param width
     */
    public void setBackgroundWidth( double width ) {
        backgroundShape.setRect( backgroundShape.getX(), backgroundShape.getY(), width, backgroundShape.getHeight() );
        backgroundNode.setPathTo( backgroundShape );
        double x = backgroundNode.getFullBoundsReference().getMaxX() - newGameButton.getFullBoundsReference().getWidth() - X_MARGIN;
        newGameButton.setOffset( x, newGameButton.getYOffset() );
    }

}
