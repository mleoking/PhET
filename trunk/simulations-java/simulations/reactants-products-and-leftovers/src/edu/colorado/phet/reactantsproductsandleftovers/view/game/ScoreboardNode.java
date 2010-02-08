/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static final Color BUTTON_COLOR = Color.WHITE;
    private static final int FONT_SIZE = 24;
    private static final PhetFont FONT = new PhetFont( FONT_SIZE );
    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 5;
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final NumberFormat ONE_DIGIT_TIME_FORMAT = new DecimalFormat( "0" );
    private static final NumberFormat TWO_DIGIT_TIME_FORMAT = new DecimalFormat( "00" );
    
    // immutable members
    private final GameModel model;
    private final PText scoreValue, levelValue, timerValue;
    private final PImage timerIcon;
    private final PPath backgroundNode;
    private final GradientButtonNode newGameButton;
    
    // mutable members
    private boolean confirmNewGame; // request confirmation when "New Game" button is pressed?
 
    public ScoreboardNode( final GameModel model ) {
        super();
        
        confirmNewGame = false;
        this.model = model;
        
        // Score
        PText scoreLabel = new PText( RPALStrings.LABEL_SCORE );
        scoreLabel.setFont( FONT );
        String maxScore = GameModel.getPerfectScore() + "/" + GameModel.getChallengesPerGame();
        scoreValue = new PText( maxScore ); // start with this, so we have max length for layout
        scoreValue.setFont( FONT );
        
        // Level
        PText levelLabel = new PText( RPALStrings.LABEL_LEVEL );
        levelLabel.setFont( FONT );
        levelValue = new PText( String.valueOf( GameModel.getLevelRange().getMax() ) );
        levelValue.setFont( FONT );
        
        // timer
        timerIcon = new PImage( RPALImages.STOPWATCH );
        timerValue = new PText( "00:00:00" ); // use this so we have max length for layout
        timerValue.setFont( FONT );
        
        // New Game button
        newGameButton = new GradientButtonNode( RPALStrings.BUTTON_NEW_GAME, FONT_SIZE, BUTTON_COLOR );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleNewGame();
            }
        } );
       
        // rendering order
        addChild( scoreLabel );
        addChild( scoreValue );
        addChild( levelLabel );
        addChild( levelValue );
        addChild( timerIcon );
        addChild( timerValue );
        addChild( newGameButton );
        
        // layout, everything in a row, vertically centered, offsets were set by eyeballing them
        final double maxChildHeight = getMaxChildHeight();
        double x = X_MARGIN;
        double y = Y_MARGIN + ( ( maxChildHeight - scoreLabel.getFullBoundsReference().getHeight() ) / 2 );
        scoreLabel.setOffset( x, y );
        x = scoreLabel.getFullBoundsReference().getMaxX() + 3;
        y = Y_MARGIN + ( ( maxChildHeight - scoreValue.getFullBoundsReference().getHeight() ) / 2 );
        scoreValue.setOffset( x, y );
        x = scoreValue.getFullBoundsReference().getMaxX() + 10;
        y = Y_MARGIN + ( ( maxChildHeight - levelLabel.getFullBoundsReference().getHeight() ) / 2 );
        levelLabel.setOffset( x, y );
        x = levelLabel.getFullBoundsReference().getMaxX() + 2;
        y = Y_MARGIN + ( ( maxChildHeight - levelValue.getFullBoundsReference().getHeight() ) / 2 );
        levelValue.setOffset( x, y );
        x = levelValue.getFullBoundsReference().getMaxX() + 80;
        y = Y_MARGIN + ( ( maxChildHeight - timerIcon.getFullBoundsReference().getHeight() ) / 2 );
        timerIcon.setOffset( x, y );
        x = timerIcon.getFullBoundsReference().getMaxX() + 4;
        y = Y_MARGIN + ( ( maxChildHeight - timerValue.getFullBoundsReference().getHeight() ) / 2 );
        timerValue.setOffset( x, y );
        x = timerValue.getFullBoundsReference().getMaxX() + 20;
        y = Y_MARGIN + ( ( maxChildHeight - newGameButton.getFullBoundsReference().getHeight() ) / 2 );
        newGameButton.setOffset( x, y );
        
        // background, added last since it's sized to fit the child nodes above
        PBounds b = getFullBoundsReference();
        backgroundNode = new PPath( new PBounds( 0, 0, b.getMaxX() + X_MARGIN, b.getMaxY() + Y_MARGIN ) );
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
                setPoints( model.getPoints() );
                
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
        setPoints( model.getPoints() );
        setLevel( model.getLevel() );
        setTimerVisible( model.isTimerVisible() );
        setTime( model.getTime() );
    }
    
    private void setPoints( double points ) {
        String pointsString = POINTS_FORMAT.format( points );
        String challengesString = String.valueOf( GameModel.getChallengesPerGame() );
        Object[] args = { pointsString, challengesString };
        String scoreString = MessageFormat.format( "{0}/{1}", args );
        scoreValue.setText( scoreString );
    }
    
    private void setLevel( int level ) {
        levelValue.setText( String.valueOf( level ) );
    }
    
    private void setTimerVisible( boolean visible ) {
        timerIcon.setVisible( visible );
        timerValue.setVisible( visible );
    }
    
    private void setTime( long elapsedMillis ) {
        int hours = (int) ( elapsedMillis / ( 1000 * 60 * 60 ) );
        int minutes = (int) ( ( elapsedMillis % ( 1000 * 60 * 60 ) ) / ( 1000 * 60 ) );
        int seconds = (int) ( ( ( elapsedMillis % ( 1000 * 60 * 60 ) ) % ( 1000 * 60 ) ) / 1000 );
        String valueString = "";
        if ( hours > 0 ) {
            // hours:minutes:seconds
            Object[] args = { ONE_DIGIT_TIME_FORMAT.format( hours ), TWO_DIGIT_TIME_FORMAT.format( minutes ), TWO_DIGIT_TIME_FORMAT.format( seconds ) };
            valueString = MessageFormat.format( "{0}:{1}:{2}", args );
        }
        else {
            // minutes:seconds
            Object[] args = { ONE_DIGIT_TIME_FORMAT.format( minutes ), TWO_DIGIT_TIME_FORMAT.format( seconds ) };
            valueString = MessageFormat.format( "{0}:{1}", args );
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
     * Changes the width of the scoreboard, while keeping the "New Game" button
     * at the right edge.  Used to adjust the scoreboard width to the play area.
     * @param width
     */
    public void setPanelWidth( double width ) {
        PBounds b = backgroundNode.getFullBoundsReference();
        backgroundNode.setPathTo( new PBounds( 0, 0, width, b.getHeight() ) );
        double x = backgroundNode.getFullBoundsReference().getMaxX() - newGameButton.getFullBoundsReference().getWidth() - X_MARGIN;
        newGameButton.setOffset( x, newGameButton.getYOffset() );
    }

}
