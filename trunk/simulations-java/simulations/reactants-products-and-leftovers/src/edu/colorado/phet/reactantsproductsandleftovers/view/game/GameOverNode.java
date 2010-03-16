/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.util.TimeUtils;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Upon completion of a Game, this node is used to display a summary of the user's game results.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameOverNode extends PhetPNode {
    
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final Font LABEL_FONT = new PhetFont( 18 );
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 5, 14, 5, 14 ) );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final int MIN_WIDTH = 200;
    
    private final JLabel levelLabel;
    private final JLabel scoreLabel;
    private final JLabel timeLabel;
    
    public GameOverNode( final GameModel model ) {
        super();
        
        // title
        JLabel titleLabel = new JLabel( RPALStrings.TITLE_GAME_OVER ); 
        titleLabel.setFont( TITLE_FONT );
        
        // horizontal separator
        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setForeground( Color.BLACK );
        
        // level
        levelLabel = new JLabel( getLevelString( model ) );
        levelLabel.setFont( LABEL_FONT );
        
        // score
        scoreLabel = new JLabel( getScoreString( model ) );
        scoreLabel.setFont( LABEL_FONT );
        
        // time
        timeLabel = new JLabel( getTimeString( model ) ); 
        timeLabel.setFont( LABEL_FONT );
        
        // horizontal separator
        JSeparator buttonSeparator = new JSeparator();
        buttonSeparator.setForeground( Color.BLACK );
        
        // buttons
        JButton newGameButton = new JButton( RPALStrings.BUTTON_NEW_GAME );
        newGameButton.setOpaque( false );
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque( false );
        buttonPanel.add( newGameButton );
        newGameButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.newGame();
            }
        });
        
        // main panel
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BORDER );
        panel.setBackground( BACKGROUND );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        layout.setMinimumWidth( 0, MIN_WIDTH );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column, 1, 1, GridBagConstraints.CENTER );
        layout.addFilledComponent( titleSeparator, row++, column, 1, 1, GridBagConstraints.HORIZONTAL );
        layout.addComponent( levelLabel, row++, column );
        layout.addComponent( scoreLabel, row++, column );
        layout.addComponent( timeLabel, row++, column );
        layout.addFilledComponent( buttonSeparator, row++, column, 1, 1, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, column, GridBagConstraints.CENTER );
        
        // wrap with PSwing after setting JLabel text values, to workaround #2171
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
    }
    
    /*
     * Gets the level string.
     */
    private static String getLevelString( GameModel model ) {
        return MessageFormat.format( RPALStrings.LABEL_LEVEL, String.valueOf( model.getLevel() ) );
    }
    
    /*
     * Gets the score string.
     * If we had a perfect score, indicate that.
     */
    private static String getScoreString( GameModel model ) {
        String pointsString = POINTS_FORMAT.format( model.getPoints() );
        String perfectScoreString = POINTS_FORMAT.format( GameModel.getPerfectScore() );
        String scoreString = null;
        if ( model.isPerfectScore() ) {
            scoreString = MessageFormat.format( RPALStrings.LABEL_SCORE_PERFECT, pointsString, perfectScoreString );
        }
        else {
            scoreString = MessageFormat.format( RPALStrings.LABEL_SCORE_IMPERFECT, pointsString, perfectScoreString );
        }
        return scoreString;
    }
    
    /*
     * Gets the time string.
     * If we had an imperfect score, simply show the time.
     * If we had a perfect score, show the best time, and indicate if the time was a "new best".
     */
    private static String getTimeString( GameModel model ) {
        String s = " ";
        if ( model.isTimerVisible() ) {
            // Time: 0:29
            String timeString = MessageFormat.format( RPALStrings.LABEL_TIME, TimeUtils.getTimeString( model.getTime() ) );
            if ( !model.isPerfectScore() ) {
                // Time: 0:29
                s = timeString;
            }
            else if ( model.isNewBestTime() ) {
                // Time: 0:29 (NEW BEST!)
                s = MessageFormat.format( RPALStrings.FORMAT_TIME_BEST, timeString, RPALStrings.LABEL_NEW_BEST );
            }
            else {
                // (Best: 0:20)
                String bestTimeString = MessageFormat.format( RPALStrings.LABEL_BEST, TimeUtils.getTimeString( model.getBestTime() ) );
                // Time: 0:29 (Best: 0:20)
                s = MessageFormat.format( RPALStrings.FORMAT_TIME_BEST, timeString, bestTimeString );
            }
        }
        return s;
    }
}
