// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * May 4, 2008 at 11:16:09 PM
 */
public class BMIHelpButtonNode extends ButtonNode {
    private Human human;

    public BMIHelpButtonNode( final Component parentComponent, final Human human ) {
        super( "?", new PhetFont( Font.BOLD, 14 ), Color.red );
        this.human = human;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String currentBMI = EatingAndExerciseResources.getString( "bmi.current" );
                String line1 = EatingAndExerciseResources.getString( "bmi.table" );
                String line2 = EatingAndExerciseResources.getString( "bmi.columns" );
                String line3 = EatingAndExerciseResources.getString( "bmi.underweight" );
                String line4 = EatingAndExerciseResources.getString( "bmi.normal" );
                String line5 = EatingAndExerciseResources.getString( "bmi.overweight" );
                String line6 = EatingAndExerciseResources.getString( "bmi.obese" );
                PhetOptionPane.showMessageDialog( parentComponent, currentBMI + new DecimalFormat( "0.0" ).format( human.getBMI() ) + "\n\n" +
                                                                   line1 +
                                                                   line2 +
                                                                   line3 +
                                                                   line4 +
                                                                   line5 +
                                                                   line6 );
            }
        } );
    }
}
