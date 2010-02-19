/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
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
public class GameSummaryNode extends PhetPNode {
    
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 5, 14, 5, 14 ) );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    
    private final GameModel model;
    private final JLabel messageLabel;
    
    public GameSummaryNode( final GameModel model ) {
        super();
        
        // text
        messageLabel = new JLabel( "?" ); // computed dynamically
        
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
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( messageLabel, row++, column );
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
        
        String s = "<html>";
        s += RPALStrings.MESSAGE_GAME_OVER;
        s += "<br><br>";
        // level
        s += MessageFormat.format( RPALStrings.LABEL_LEVEL, String.valueOf( model.getLevel() ) );
        s += "<br><br>";
        // score
        if ( model.isPerfectScore() ) {
            s += MessageFormat.format( RPALStrings.LABEL_PERFECT_SCORE, POINTS_FORMAT.format( model.getPoints() ), POINTS_FORMAT.format( GameModel.getPerfectScore() ) );
        }
        else {
            s += MessageFormat.format( RPALStrings.LABEL_FINAL_SCORE, POINTS_FORMAT.format( model.getPoints() ), POINTS_FORMAT.format( GameModel.getPerfectScore() ) );
        }
        s += "<br><br>";
        // time
        if ( model.isTimerVisible() ) {
            if ( !model.isPerfectScore() ) {
                s += MessageFormat.format( RPALStrings.LABEL_TIME, TimeUtils.getTimeString( model.getTime() ) );
            }
            else if ( model.isNewBestTime() ) {
                s += MessageFormat.format( RPALStrings.LABEL_NEW_BEST_TIME, TimeUtils.getTimeString( model.getTime() ) );
            }
            else {
                s += MessageFormat.format( RPALStrings.LABEL_NOT_BEST_TIME, TimeUtils.getTimeString( model.getTime() ), TimeUtils.getTimeString( model.getBestTime() ) );
            }
        }
        s += "<br><br>";
        
        s += "&nbsp;"; //XXX workaround, PSwing cuts off last char
        s+= "</html>";
        
        messageLabel.setText( s );
    }
}
