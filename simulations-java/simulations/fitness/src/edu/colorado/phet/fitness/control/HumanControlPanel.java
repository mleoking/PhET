package edu.colorado.phet.fitness.control;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.glaciers.control.HorizontalLayoutStrategy;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private Human human;

    public HumanControlPanel( final Human human ) {
        this.human = human;
        JTextField name = new JTextField( human.getName() );
        add( name );

        LinearValueControl age = new LinearValueControl( 0, 100 * 525600.0 * 60, human.getAge(), "Age", "0.00", "seconds", new HorizontalLayoutStrategy() );

        age.getTextField().setColumns( 10 );
        add( age );

        double minHeight = 1;
        double maxHeight = 2.72;
        final LinearValueControl heightControl = new LinearValueControl( minHeight, maxHeight, human.getHeight(), "Height", "0.00", "meters", new HorizontalLayoutStrategy() );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setHeight( heightControl.getValue() );
            }
        } );
        add( heightControl );

        double minWeight = 1;
        double maxWeight = 100;
        final LinearValueControl weightControl = new LinearValueControl( minWeight, maxWeight, human.getWeight(), "Weight", "0.00", "kg", new HorizontalLayoutStrategy() );
        weightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setWeight( weightControl.getValue() );
            }
        } );
        add( weightControl );

        Human maxBMIHuman = new Human( 0, minHeight, maxWeight, Human.Gender.MALE, "max" );
        Human minBMIHuman = new Human( 0, maxHeight, minWeight, Human.Gender.MALE, "minnie" );
        final LinearValueControl bmi = new LinearValueControl( minBMIHuman.getBMI(), maxBMIHuman.getBMI(), human.getBMI(), "BMI", "0.00", "kg/m^2", new HorizontalLayoutStrategy() );
//        bmi.setEnabled( false );
        bmi.getTextField().setEditable( false );
        bmi.getSlider().setEnabled( false );
//        add( bmi );

        human.addListener( new Human.Adapter() {
            public void bmiChanged() {
                bmi.setValue( human.getBMI() );
            }
        } );
    }

}
