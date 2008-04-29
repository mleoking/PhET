package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.model.Human;

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

        JPanel activityLevels = new ActivityLevelsPanel();
        add( activityLevels, new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ), 0 );
        add( calorieSelectionPanel, new GridBagConstraints( 0, 1, 1, 1, 1, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
    }

    public void addListener( CalorieSelectionPanel.Listener listener ) {
        calorieSelectionPanel.addListener( listener );
    }

    private class ActivityLevelsPanel extends JPanel {
        private ActivityLevelsPanel() {
            setBorder( CalorieSelectionPanel.createTitledBorder( "Lifestyle" ) );
            ButtonGroup bg = new ButtonGroup();
            for ( int i = 0; i < Activity.DEFAULT_ACTIVITY_LEVELS.length; i++ ) {
                JRadioButton jRadioButton = new JRadioButton( Activity.DEFAULT_ACTIVITY_LEVELS[i].toString(),
                                                              Activity.DEFAULT_ACTIVITY_LEVELS[i].getValue() == human.getActivityLevel() );
                final int i1 = i;
                jRadioButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        human.setActivityLevel( Activity.DEFAULT_ACTIVITY_LEVELS[i1].getValue() );
                    }
                } );
                jRadioButton.setFont( new PhetDefaultFont( 13, true ) );
                bg.add( jRadioButton );
                add( jRadioButton );
            }
        }
    }
}
