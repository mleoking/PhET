// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.developer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.eatingandexercise.model.MuscleAndFatMassLoss2;
import edu.colorado.phet.eatingandexercise.model.MuscleGainedFromExercising;

/**
 * Created by: Sam
 * Aug 5, 2008 at 3:01:31 PM
 */
public class DeveloperFrame extends JFrame {
    public DeveloperFrame() {
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        final LinearValueControl slider = new LinearValueControl( 0, 1, MuscleAndFatMassLoss2.FRACTION_FAT_LOST, "fraction fat", "0.0000", "" );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                MuscleAndFatMassLoss2.FRACTION_FAT_LOST = slider.getValue();
            }
        } );
        verticalLayoutPanel.add( slider );
        final JCheckBox jCheckBox = new JCheckBox( "Use Muscle Gained From Exercising Algorithm", MuscleGainedFromExercising.enabled );
        jCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                MuscleGainedFromExercising.enabled = jCheckBox.isSelected();
            }
        } );
        verticalLayoutPanel.add( jCheckBox );


        final JCheckBox asymmetry = new JCheckBox( "all fat when gaining weight (asymmetric)", MuscleAndFatMassLoss2.allFatWhenGainingWeight );
        asymmetry.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                MuscleAndFatMassLoss2.allFatWhenGainingWeight = asymmetry.isSelected();
            }
        } );
        verticalLayoutPanel.add( asymmetry );


        setContentPane( verticalLayoutPanel );
        pack();
    }
}
