package edu.colorado.phet.reactantsproductsandleftovers.view;

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
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;


public class ScoreboardPanel extends JPanel {
    
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLUE, 4 ), new CompoundBorder( new LineBorder( Color.BLACK, 2 ), new EmptyBorder( 5, 14, 5, 14 ) ) );
    private static final Color BACKGROUND = Color.YELLOW;
    private static final PhetFont FONT = new PhetFont( 16 );
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.0" );
    private static final NumberFormat ONE_DIGIT_TIME_FORMAT = new DecimalFormat( "0" );
    private static final NumberFormat TWO_DIGIT_TIME_FORMAT = new DecimalFormat( "00" );
    
    private final GameModel model;
    private final JLabel scoreValue, levelValue, timerValue;
    private final JLabel timerIcon;
    private final JButton newGameButton;
 
    public ScoreboardPanel( final GameModel model ) {
        super();
        setBorder( BORDER );
        setBackground( BACKGROUND );
        
        this.model = model;
        
        // Score
        JLabel scoreLabel = new JLabel( RPALStrings.LABEL_SCORE );
        scoreLabel.setFont( FONT );
        scoreValue = new JLabel( "10/10" ); //XXX compute this so we have max length for layout
        scoreValue.setFont( FONT );
        
        // Level
        JLabel levelLabel = new JLabel( RPALStrings.LABEL_LEVEL );
        levelLabel.setFont( FONT );
        levelValue = new JLabel( String.valueOf( GameModel.getLevelRange().getMax() ) );
        levelValue.setFont( FONT );
        
        // timer
        timerIcon = new JLabel( new ImageIcon( RPALImages.STOPWATCH ) );
        timerValue = new JLabel( "00:00:00" ); //XXX compute this so we have max length for layout
        timerValue.setFont( FONT );
        
        // New Game!
        newGameButton = new JButton( RPALStrings.BUTTON_NEW_GAME );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleNewGame();
            }
        } );
        
        // layout, single row
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( scoreLabel, row, column++ );
        layout.setMinimumWidth( column, scoreValue.getPreferredSize().width );
        layout.addComponent( scoreValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 20 ), row, column++ );
        layout.addComponent( levelLabel, row, column++ );
        layout.setMinimumWidth( column, levelValue.getPreferredSize().width );
        layout.addComponent( levelValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 20 ), row, column++ );
        layout.setMinimumWidth( column, timerIcon.getPreferredSize().width );
        layout.addComponent( timerIcon, row, column );
        layout.setMinimumWidth( column, timerIcon.getPreferredSize().width ); // permanent space for timer icon
        layout.setMinimumHeight( row, timerIcon.getPreferredSize().height );   // permanent space for timer icon
        column++;
        layout.addComponent( timerValue, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 20 ), row, column++ );
        layout.addComponent( newGameButton, row, column++ );
        
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
            public void timerEnabledChanged() {
                setTimerVisible( model.isTimerEnabled() );
                if ( model.isTimerEnabled() ) {
                    setTime( model.getElapsedTime() );
                }
            }
            
            @Override
            public void timeChanged() {
                setTime( model.getElapsedTime() );
            }
        });
        
        // initial state
        setPoints( model.getPoints() );
        setLevel( model.getLevel() );
        setTimerVisible( model.isTimerEnabled() );
        setTime( model.getElapsedTime() );
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
        model.endGame();
    }

}
