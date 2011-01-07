// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Apr 23, 2008 at 8:02:01 AM
 */
public class NamePanel extends JPanel {
    public NamePanel( Human human ) {

        JPanel namePanel = new JPanel();
        namePanel.add( new JLabel( EatingAndExerciseResources.getString( "name" ) ) );
        JTextField name = new JTextField( human.getName() );
        name.setColumns( 10 );
        namePanel.add( name );
        add( namePanel );
    }
}
