/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.*;
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
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
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
    
    private final GameModel model;
    private final JLabel levelLabel;
    private final JLabel scoreLabel;
    private final JLabel timeLabel;
    
    public GameOverNode( final GameModel model ) {
        super();
        
        // title
        JLabel titleLabel = new JLabel( RPALStrings.MESSAGE_GAME_OVER ); 
        titleLabel.setFont( TITLE_FONT );
        
        // horizontal separator
        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setForeground( Color.BLACK );
        
        // level
        levelLabel = new JLabel( "?" ); // compute dynamically
        levelLabel.setFont( LABEL_FONT );
        
        // score
        scoreLabel = new JLabel( "?" ); // compute dynamically
        scoreLabel.setFont( LABEL_FONT );
        
        // time
        timeLabel = new JLabel( "?" ); // compute dynamically
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
        
        // PSwing
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        // sync with model
        this.model = model;
        model.addGameListener( new GameAdapter() {
            @Override
            public void gameCompleted() {
                update();
            }
        });
        
        // default state, to give this a general size for layout purposes
        update();
    }
    
    private void update() {
        
        // level
        levelLabel.setText( MessageFormat.format( RPALStrings.LABEL_LEVEL, String.valueOf( model.getLevel() ) ) );
        
        // score
        String scoreString = POINTS_FORMAT.format( model.getPoints() );
        String perfectScoreString = POINTS_FORMAT.format( GameModel.getPerfectScore() );
        if ( model.isPerfectScore() ) {
            scoreLabel.setText( MessageFormat.format( RPALStrings.LABEL_PERFECT_SCORE, scoreString, perfectScoreString ) );
        }
        else {
            scoreLabel.setText( MessageFormat.format( RPALStrings.LABEL_FINAL_SCORE, scoreString, perfectScoreString ) );
        }

        // time
        if ( model.isTimerVisible() ) {
            String timeString = TimeUtils.getTimeString( model.getTime() );
            String bestTimeString = TimeUtils.getTimeString( model.getBestTime() );
            if ( !model.isPerfectScore() ) {
                timeLabel.setText( MessageFormat.format( RPALStrings.LABEL_TIME, timeString ) );
            }
            else if ( model.isNewBestTime() ) {
                timeLabel.setText( MessageFormat.format( RPALStrings.LABEL_NEW_BEST_TIME, bestTimeString ) );
            }
            else {
                timeLabel.setText( MessageFormat.format( RPALStrings.LABEL_NOT_BEST_TIME, timeString, bestTimeString ) );
            }
        }
        else {
            timeLabel.setText( " " );
        }
    }
}
