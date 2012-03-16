// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;

/**
 * The dialog that is shown when all of the bunnies die. Includes a "play again" button that will reset the simulation
 *
 * @author Jonathan Olson
 */
public class GameOverDialog extends PaintImmediateDialog {

    public GameOverDialog( Frame frame, final NaturalSelectionModule module ) {
        super( frame );

        setTitle( NaturalSelectionStrings.GAME_OVER );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();

        panel.setInsets( new Insets( 10, 10, 0, 10 ) );

        JLabel label = new JLabel( NaturalSelectionStrings.GAME_OVER_ALL_BUNNIES_DIED );
        panel.add( label );

        panel.setInsets( new Insets( 10, 10, 10, 10 ) );

        JButton playAgainButton = new JButton( NaturalSelectionStrings.GAME_OVER_PLAY_AGAIN );
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
