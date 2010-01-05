package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolox.pswing.PSwing;


public class ScoreboardNode extends PhetPNode {
    
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 2, 14, 2, 14 ) );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final PhetFont FONT = new PhetFont( 24 );
    private static final int X_SPACING = 40;
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final NumberFormat ONE_DIGIT_TIME_FORMAT = new DecimalFormat( "0" );
    private static final NumberFormat TWO_DIGIT_TIME_FORMAT = new DecimalFormat( "00" );
    
    private final GameModel model;
    private final JLabel scoreValue, levelValue, timerValue;
    private final JLabel timerIcon;
    private final JButton newGameButton;
 
    public ScoreboardNode( final GameModel model ) {
        super();
        
        this.model = model;
        
        // Score
        JLabel scoreLabel = new JLabel( RPALStrings.LABEL_SCORE );
        scoreLabel.setFont( FONT );
        String maxScore = GameModel.getPerfectScore() + "/" + GameModel.getChallengesPerGame();
        scoreValue = new JLabel( maxScore ); // start with this, so we have max length for layout
        scoreValue.setFont( FONT );
        
        // Level
        JLabel levelLabel = new JLabel( RPALStrings.LABEL_LEVEL );
        levelLabel.setFont( FONT );
        levelValue = new JLabel( String.valueOf( GameModel.getLevelRange().getMax() ) );
        levelValue.setFont( FONT );
        
        // timer
        timerIcon = new JLabel( new ImageIcon( RPALImages.STOPWATCH ) );
        timerValue = new JLabel( "00:00:00" ); // use this so we have max length for layout
        timerValue.setFont( FONT );
        
        // New Game!
        newGameButton = new JButton( RPALStrings.BUTTON_NEW_GAME );
        newGameButton.setFont( FONT );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleNewGame();
            }
        } );
        
        // panel, layout in a single row
        JPanel panel = new JPanel();
        panel.setBorder( BORDER );
        panel.setBackground( BACKGROUND );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( scoreLabel, row, column++ );
        layout.setMinimumWidth( column, scoreValue.getPreferredSize().width );
        layout.addComponent( scoreValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( X_SPACING ), row, column++ );
        layout.addComponent( levelLabel, row, column++ );
        layout.setMinimumWidth( column, levelValue.getPreferredSize().width );
        layout.addComponent( levelValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( X_SPACING ), row, column++ );
        layout.setMinimumWidth( column, timerIcon.getPreferredSize().width );
        layout.addComponent( timerIcon, row, column );
        layout.setMinimumWidth( column, timerIcon.getPreferredSize().width ); // permanent space for timer icon
        layout.setMinimumHeight( row, timerIcon.getPreferredSize().height );   // permanent space for timer icon
        column++;
        layout.addComponent( timerValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( X_SPACING ), row, column++ );
        layout.addComponent( newGameButton, row, column++ );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
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
        long elapsedSec = ( elapsedMillis / 1000 );
        int secondsPerMinute = 60;
        int minutesPerHour = 60;
        int secondsPerHour = minutesPerHour * secondsPerMinute;
        int hours = (int) ( elapsedSec / secondsPerHour );
        int minutes = (int) ( elapsedSec - ( hours * secondsPerHour ) ) / secondsPerMinute;
        int seconds = (int) ( elapsedSec - ( hours * secondsPerHour ) - ( minutes * secondsPerMinute ) );
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
        //XXX what else?...
        model.newGame();
    }

}
