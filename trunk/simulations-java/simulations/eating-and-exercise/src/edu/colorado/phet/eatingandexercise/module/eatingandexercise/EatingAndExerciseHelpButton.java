package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;

/**
 * Created by: Sam
 * Aug 15, 2008 at 11:11:24 AM
 */
public class EatingAndExerciseHelpButton extends JButton {
    public EatingAndExerciseHelpButton() {
        super( getHelpString() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( EatingAndExerciseHelpButton.this, EatingAndExerciseResources.getString( "help.message" ), getHelpString(), JOptionPane.INFORMATION_MESSAGE );
            }
        } );
    }

    private static String getHelpString() {
        return EatingAndExerciseResources.getCommonString( PhetCommonResources.STRING_HELP_MENU_HELP );
    }
}
