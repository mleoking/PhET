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
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLUE, 4 ), new CompoundBorder( new LineBorder( Color.BLACK, 2 ), new EmptyBorder( 14, 14, 14, 14 ) ) );
    private static final Color BACKGROUND = Color.YELLOW;
    
    private final GameModel model;
    private final JLabel summaryLabel;
    
    public GameSummaryNode( final GameModel model ) {
        super();
        
        // message
        summaryLabel = new JLabel();
        
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
        layout.addComponent( summaryLabel, 0, 0 );
        layout.addAnchoredComponent( buttonPanel, 1, 0, GridBagConstraints.CENTER );
        
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
        String pointsString = POINTS_FORMAT.format( model.getPoints() );
        String challengesString = String.valueOf( GameModel.getChallengesPerGame() );
        Object[] args = { pointsString, challengesString };
        String scoreString = MessageFormat.format( "{0}/{1}", args );
        //XXX i18n of text
        String text = "<html>Game Over!<br><br>Your final score is " + scoreString;
        if ( model.getPoints() == model.getPerfectScore() ) {
            text += "<br><br>You have a perfect score! Woo hoo!";
        }
        text += "</html>";
        summaryLabel.setText( text ); 
    }
}
