package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AlignedSliderSetLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.DefaultLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.fitness.model.Human;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private Human human;

    public HumanControlPanel( final Human human ) {
        this.human = human;
        getGridBagConstraints().insets = new Insets( 4, 4, 4, 4 );
        setFillNone();
        JPanel namePanel = new JPanel();
        namePanel.add( new JLabel( "Name: " ) );
        JTextField name = new JTextField( human.getName() );
        name.setColumns( 10 );
        namePanel.add( name );
        add( namePanel );

        add( new GenderControl( human ) );
        setFillHorizontal();

        final LinearValueControl age = new HumanSlider( 0, 100 * 525600.0 * 60, human.getAge(), "Age", "0.00", "seconds" );
        age.getTextField().setColumns( 9 );
        add( age );
        age.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setAge( age.getValue() );
            }
        } );
        human.addListener( new Human.Adapter(){
            public void ageChanged() {
                age.setValue( human.getAge() );
            }
        } );

        double minHeight = 1;
        double maxHeight = 2.72;
        final LinearValueControl heightControl = new HumanSlider( minHeight, maxHeight, human.getHeight(), "Height", "0.00", "meters" );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setHeight( heightControl.getValue() );
            }
        } );

        add( heightControl );

        double minWeight = 1;
        double maxWeight = 100;
        final LinearValueControl weightControl = new HumanSlider( minWeight, maxWeight, human.getMass(), "Weight", "0.00", "kg" );
        weightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( weightControl.getValue() );
            }
        } );
        human.addListener( new Human.Adapter(){
            public void weightChanged() {
                weightControl.setValue( human.getMass() );
            }
        } );
        add( weightControl );

        final LinearValueControl muscle = new HumanSlider( 0, 100, human.getLeanMuscleMass(), "Lean Muscle Mass", "0.0", "kg" );
        add( muscle );
//        final LinearValueControl fat = new HumanSlider( 0, 100, human.getFatPercent(), "Fat", "0.0", "kg" );
//        add( fat );

        muscle.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setLeanMuscleMass( muscle.getValue() );
            }
        } );
//        fat.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                human.setFatPercent( fat.getValue() );
//            }
//        } );
        human.addListener( new Human.Adapter() {
            public void musclePercentChanged() {
                muscle.setValue( human.getLeanMuscleMass() );
            }

//            public void fatPercentChanged() {
//                fat.setValue( human.getFatPercent() );
//            }
        } );

//        Human maxBMIHuman = new Human( 0, minHeight, maxWeight, Human.Gender.MALE, "max" );
//        Human minBMIHuman = new Human( 0, maxHeight, minWeight, Human.Gender.MALE, "minnie" );
//        final LinearValueControl bmi = new LinearValueControl( minBMIHuman.getBMI(), maxBMIHuman.getBMI(), human.getBMI(), "BMI", "0.00", "kg/m^2" );
////        bmi.setEnabled( false );
//        bmi.getTextField().setEditable( false );
//        bmi.getSlider().setEnabled( false );
////        add( bmi );

//        LinearValueControl[] hs = new LinearValueControl[]{age, heightControl, weightControl, muscle, fat};
        LinearValueControl[] hs = new LinearValueControl[]{age, heightControl, weightControl, muscle};
        new AlignedSliderSetLayoutStrategy( hs ).doLayout();
//        human.addListener( new Human.Adapter() {
//            public void bmiChanged() {
//                bmi.setValue( human.getBMI() );
//            }
//        } );
    }

    public static final class HumanSlider extends LinearValueControl {

        public HumanSlider( double minWeight, double maxWeight, double weight, String s, String s1, String s2 ) {
            super( minWeight, maxWeight, weight, s, s1, s2, new DefaultLayoutStrategy() );
            getSlider().setPaintLabels( false );
            getSlider().setPaintTicks( false );
        }
    }

    private class GenderControl extends JPanel {
        public GenderControl( final Human human ) {
            setLayout( new FlowLayout() );
            final JRadioButton femaleButton = new JRadioButton( "Female", human.getGender() == Human.Gender.FEMALE );
            femaleButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    human.setGender( Human.Gender.FEMALE );
                }
            } );
            add( femaleButton );
            final JRadioButton maleButton = new JRadioButton( "Male", human.getGender() == Human.Gender.MALE );
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
}