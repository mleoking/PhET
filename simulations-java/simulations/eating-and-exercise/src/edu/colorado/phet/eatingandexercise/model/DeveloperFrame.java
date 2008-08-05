package edu.colorado.phet.eatingandexercise.model;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

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
        setContentPane( verticalLayoutPanel );

        pack();
    }
}
