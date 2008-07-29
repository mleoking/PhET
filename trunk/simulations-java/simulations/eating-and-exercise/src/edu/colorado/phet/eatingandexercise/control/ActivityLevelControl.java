package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;

/**
 * Created by: Sam
 * Jul 1, 2008 at 2:18:21 PM
 */
public class ActivityLevelControl extends JPanel {
    public ActivityLevelControl( final EatingAndExerciseCanvas canvas, Human human ) {
        add( new ActivityLevelComboBox( canvas, human ) );
        final JButton button = new JButton( "?" );
        add( button );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( canvas, getExplanationText() );
            }
        } );
    }

    String[] activityLevelKeys = new String[]{
            "very-sedentary",
            "sedentary",
            "moderate",
            "active",
    };

    private String getExplanationText() {
        String string = "<html>";
        for ( int i = 0; i < activityLevelKeys.length; i++ ) {
            String key = EatingAndExerciseResources.getString( "activity." + activityLevelKeys[i] );
            String desc = EatingAndExerciseResources.getString( "activity." + activityLevelKeys[i] + ".description" );
            string += key + ": " + desc + "<br><br><br>";
        }
        return string + "</html>";
    }
}
