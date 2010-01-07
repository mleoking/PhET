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
import edu.umd.cs.piccolox.pswing.PSwing;


public class GameSummaryNode extends PhetPNode {
    
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 5, 14, 5, 14 ) );
    private static final Color BACKGROUND = Color.YELLOW;
    
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
        
        // score message
        String pointsString = POINTS_FORMAT.format( model.getPoints() );
        String challengesString = String.valueOf( GameModel.getChallengesPerGame() );
        String scoreString = MessageFormat.format( "{0}/{1}", pointsString, challengesString );
        s += "<br><br>";
        s += MessageFormat.format( RPALStrings.MESSAGE_FINAL_SCORE, scoreString );
        
        // visibility of "perfect score" message
        if ( model.getPoints() == GameModel.getPerfectScore() ) {
            s += "<br><br>";
            s += RPALStrings.MESSAGE_PERFECT_SCORE;
            s += "&nbsp;"; //XXX workaround, PSwing cuts off last char
        }
        s+= "</html>";
        
        messageLabel.setText( s );
    }
}
