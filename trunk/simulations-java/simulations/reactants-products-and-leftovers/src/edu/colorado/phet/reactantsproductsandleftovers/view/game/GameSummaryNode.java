package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BorderLayout;
import java.awt.Color;
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
        
        summaryLabel = new JLabel();
        
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
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BORDER );
        panel.setBackground( BACKGROUND );
        panel.add( summaryLabel, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        this.model = model;
        model.addGameListener( new GameAdapter() {
            @Override
            public void gameCompleted() {
                update();
            }
        });
        
        update();
    }
    
    private void update() {
        String pointsString = POINTS_FORMAT.format( model.getPoints() );
        String challengesString = String.valueOf( GameModel.getChallengesPerGame() );
        Object[] args = { pointsString, challengesString };
        String scoreString = MessageFormat.format( "{0}/{1}", args );
        summaryLabel.setText( "<html>Game Over!<br>Your final score is " + scoreString + "</html>"); //XXX i18n
    }
}
