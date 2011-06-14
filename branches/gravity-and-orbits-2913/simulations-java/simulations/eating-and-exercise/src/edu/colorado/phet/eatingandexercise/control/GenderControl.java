// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 27, 2008 at 10:13:45 AM
 */
public class GenderControl extends JPanel {
    public GenderControl( final Human human ) {
        setLayout( new FlowLayout() );
        final JRadioButton femaleButton = new JRadioButton( EatingAndExerciseResources.getString( "gender.female" ), human.getGender() == Human.Gender.FEMALE );
        femaleButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                human.setGender( Human.Gender.FEMALE );
            }
        } );
        add( femaleButton );
        final JRadioButton maleButton = new JRadioButton( EatingAndExerciseResources.getString( "gender.male" ), human.getGender() == Human.Gender.MALE );
        maleButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                human.setGender( Human.Gender.MALE );
            }
        } );
        add( maleButton );
        human.addListener( new Human.Adapter() {
            public void genderChanged() {
                femaleButton.setSelected( human.getGender() == Human.Gender.FEMALE );
                maleButton.setSelected( human.getGender() == Human.Gender.MALE );
            }
        } );
    }
}
