// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Apr 24, 2008 at 2:39:39 AM
 */
public class ExerciseSelectionPanel extends JPanel implements ICalorieSelectionPanel {
    private Human human;
    private CalorieSelectionPanel calorieSelectionPanel;

    public ExerciseSelectionPanel( final Human human, final CalorieSet available, final CalorieSet selected, String availableTitle, String selectedTitle ) {
        setLayout( new GridBagLayout() );
        calorieSelectionPanel = new CalorieSelectionPanel( available, selected, availableTitle, selectedTitle );
        this.human = human;

//        JPanel activityLevels = new ActivityControlPanel( human );
//        add( activityLevels, new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ), 0 );
        add( calorieSelectionPanel, new GridBagConstraints( 0, 1, 1, 1, 1, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
    }

    public void addListener( CalorieSelectionPanel.Listener listener ) {
        calorieSelectionPanel.addListener( listener );
    }

}
