// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Apr 29, 2008 at 7:53:15 AM
 */
public class FoodSelectionPanel extends JPanel implements ICalorieSelectionPanel {
    private CalorieSelectionPanel selectionPanel;
    private Human human;

    public FoodSelectionPanel( Human human, CalorieSet available, CalorieSet calorieSet, String availableTitle, String selectedTitle ) {
        super( new GridBagLayout() );
        this.human = human;
        selectionPanel = new CalorieSelectionPanel( available, calorieSet, availableTitle, selectedTitle );

        JPanel activityLevels = new ActivityLevelsPanel();
        add( activityLevels, new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ), 0 );
        add( selectionPanel, new GridBagConstraints( 0, 1, 1, 1, 1, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
    }

    public void addListener( CalorieSelectionPanel.Listener listener ) {
        selectionPanel.addListener( listener );
    }

    private class ActivityLevelsPanel extends JPanel {
        private ActivityLevelsPanel() {
            setBorder( CalorieSelectionPanel.createTitledBorder( EatingAndExerciseResources.getString( "diet.base" ) ) );
            ButtonGroup bg = new ButtonGroup();
            final CaloricFoodItem[] baseDiets = new CaloricFoodItem[]{
                    new CaloricFoodItem( EatingAndExerciseResources.getString( "diet.nothing" ), null, 0, 0, 0, false ),
                    human.getDefaultIntake(),
            };
            for ( int i = 0; i < baseDiets.length; i++ ) {
                JRadioButton jRadioButton = new JRadioButton( baseDiets[i].getName(),
                                                              human.getSelectedFoods().contains( baseDiets[i] ) );
                final int i1 = i;
                jRadioButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        human.getSelectedFoods().removeAll( baseDiets );
                        human.getSelectedFoods().addItem( baseDiets[i1] );
                    }
                } );
                jRadioButton.setFont( new PhetFont( 13, true ) );
                bg.add( jRadioButton );
                add( jRadioButton );
            }
        }
    }
}
