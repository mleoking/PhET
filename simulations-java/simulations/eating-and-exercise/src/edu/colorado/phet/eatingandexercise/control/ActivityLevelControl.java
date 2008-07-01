package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;

/**
 * Created by: Sam
 * Jul 1, 2008 at 2:18:21 PM
 */
public class ActivityLevelControl extends JPanel {
    public ActivityLevelControl( EatingAndExerciseCanvas canvas, Human human ) {
        add( new JLabel( "Activity Level" ) );
        add( new ActivityLevelComboBox( canvas, human ) );
        final JButton button = new JButton( "?" );
        add( button );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( button, "Explanation of activity levels goes here" );
            }
        } );
    }
}
