package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 26, 2008 at 11:57:34 AM
 */
public class ActivityLevelComboBox extends JComboBox {
    public ActivityLevelComboBox( final Human human ) {
        setBorder( CalorieSelectionPanel.createTitledBorder( EatingAndExerciseResources.getString( "exercise.lifestyle" ) ) );
        for ( int i = 0; i < Activity.DEFAULT_ACTIVITY_LEVELS.length; i++ ) {
            final int i1 = i;
            addItem( Activity.DEFAULT_ACTIVITY_LEVELS[i1] );
        }
        setFont( new PhetFont( 13, true ) );
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                Activity activity = (Activity) e.getItem();
                human.setActivityLevel( activity.getValue() );
            }
        } );
    }

    public void showPopup() {
        super.showPopup();
    }
}