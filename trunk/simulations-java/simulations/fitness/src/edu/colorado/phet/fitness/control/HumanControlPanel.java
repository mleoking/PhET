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

//        add (new NamePanel(human));

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
        human.addListener( new Human.Adapter() {
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

        double minWeight = 0;
        double maxWeight = 560;//world record
        final LinearValueControl weightControl = new HumanSlider( minWeight, maxWeight, human.getMass(), "Weight", "0.00", "kg" );
        weightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( weightControl.getValue() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                weightControl.setValue( human.getMass() );
            }
        } );
        add( weightControl );

        final LinearValueControl fatMassPercent = new HumanSlider( 0, 100, human.getFatMassPercent(), "Fat Mass", "0.00", "%" );
        fatMassPercent.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setFatMassPercent( fatMassPercent.getValue() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                fatMassPercent.setValue( human.getFatMassPercent() );
            }
        } );
        add( fatMassPercent );

        final LinearValueControl fatFreeMassPercent = new HumanSlider( 0, 100, human.getFatFreeMassPercent(), "Fat Free Mass", "0.00", "%" );
        fatFreeMassPercent.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setFatMassPercent( 100 - fatFreeMassPercent.getValue() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                fatFreeMassPercent.setValue( human.getFatFreeMassPercent() );
            }
        } );
        add( fatFreeMassPercent );

        LinearValueControl[] hs = new LinearValueControl[]{age, heightControl, weightControl, fatMassPercent, fatFreeMassPercent};
        new AlignedSliderSetLayoutStrategy( hs ).doLayout();
    }

    public static final class HumanSlider extends LinearValueControl {

        public HumanSlider( double min, double max, double value, String label, String textFieldPattern, String units ) {
            super( min, max, value, label, textFieldPattern, units, new DefaultLayoutStrategy() );
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