package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;

public class GameOverDialog extends JDialog {

    public GameOverDialog( Frame frame, final NaturalSelectionModule module ) {
        super( frame );

        setTitle( "Game Over" );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();

        panel.setInsets( new Insets( 10, 10, 0, 10 ) );

        JLabel label = new JLabel( "All of the bunnies died!" );
        panel.add( label );

        panel.setInsets( new Insets( 10, 10, 10, 10 ) );

        JButton playAgainButton = new JButton( "Play Again" );
        panel.add( playAgainButton );

        playAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.reset();
                GameOverDialog.this.dispose();
            }
        } );

        setContentPane( panel );

        pack();
    }

}
