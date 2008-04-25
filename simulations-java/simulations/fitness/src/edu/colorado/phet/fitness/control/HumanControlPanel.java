package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.DefaultLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AlignedSliderSetLayoutStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.model.FitnessUnits;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.colorado.phet.fitness.view.SliderNode;
import edu.colorado.phet.fitness.view.FitnessColorScheme;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private FitnessModel model;
    private Human human;

//    public String toString( double sec ) {
//        int years = (int) FitnessUnits.secondsToYears( sec );
//        double remainingSec = sec - FitnessUnits.yearsToSeconds( years );
//        int months=(int) FitnessUnits
//    }

    public HumanControlPanel( final FitnessModel model, final Human human ) {
        this.model = model;
        this.human = human;
        getGridBagConstraints().insets = new Insets( 4, 4, 4, 4 );
        setFillNone();

        add( new GenderControl( human ) );
        setFillHorizontal();

        final HumanSlider age = new HumanSlider( 0, 100, FitnessUnits.secondsToYears( human.getAge() ), "Age", "0.00", "years" );
        age.setColumns( 5 );
        add( age );
        age.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setAge( FitnessUnits.yearsToSeconds( age.getValue() ) );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                age.setValue( FitnessUnits.secondsToYears( human.getAge() ) );
//                age.setValue( human.getAge() );
            }
        } );

        //todo: factor out slider that accommodates units
        final double minHeight = 1;
        final double maxHeight = 2.72;
        final HumanSlider heightControl = new HumanSlider( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ), model.getUnits().modelToViewDistance( human.getHeight() ), "Height", "0.00", model.getUnits().getDistanceUnit() );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double v = model.getUnits().viewToModelDistance( heightControl.getValue() );
                human.setHeight( v );
            }
        } );
        model.addListener( new FitnessModel.Adapter() {
            public void unitsChanged() {
                double value = model.getUnits().modelToViewDistance( human.getHeight() );

                //have to change range before changing value
                heightControl.setRange( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ) );
                heightControl.setValue( value );
                heightControl.setUnits( model.getUnits().getDistanceUnit() );

                heightControl.setPaintLabels( false );
                heightControl.setPaintTicks( false );
            }
        } );

        add( heightControl );

        final double minWeight = 0;
        final double maxWeight = 560;//world record
        final HumanSlider weightControl = new HumanSlider( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ), model.getUnits().modelToViewMass( human.getMass() ), "Weight", "0.00", model.getUnits().getMassUnit() );
        weightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( model.getUnits().viewToModelMass( weightControl.getValue() ) );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                weightControl.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
            }
        } );
        model.addListener( new FitnessModel.Adapter() {
            public void unitsChanged() {
                weightControl.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
                weightControl.setUnits( model.getUnits().getMassUnit() );
                weightControl.setRange( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ) );
                weightControl.setPaintLabels( false );
                weightControl.setPaintTicks( false );
            }
        } );
        add( weightControl );

        final HumanSlider fatMassPercent = new HumanSlider( 0, 100, human.getFatMassPercent(), "Fat Mass", "0.00", "%" );
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

        final HumanSlider fatFreeMassPercent = new HumanSlider( 0, 100, human.getFatFreeMassPercent(), "Fat Free Mass", "0.00", "%" );
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
            setSignifyOutOfBounds( false );
        }

        public void setColumns( int i ) {
            getTextField().setColumns( i );
        }

        public void setPaintLabels( boolean b ) {
            getSlider().setPaintLabels( b );
        }

        public void setPaintTicks( boolean b ) {
            getSlider().setPaintTicks( b );
        }
    }

    public static final class HumanSliderNEW extends PhetPCanvas {
        private SliderNode sliderNode;

        public HumanSliderNEW( double min, double max, double value, String label, String textFieldPattern, String units ) {
//            super( min, max, value, label, textFieldPattern, units, new DefaultLayoutStrategy() );
            sliderNode = new SliderNode( min, max, value );
            setBorder( null );
            setBackground( FitnessColorScheme.getBackgroundColor() );
            sliderNode.setOffset( -sliderNode.getFullBounds().getX()+1, -sliderNode.getFullBounds().getY()+1);
            addScreenChild( sliderNode );
            setPreferredSize( new Dimension( (int)sliderNode.getFullBounds().getWidth()+2, (int) sliderNode.getFullBounds().getHeight() +2) );
        }

        public void setColumns( int col ) {
        }

        public double getValue() {
            return sliderNode.getValue();
        }

        public void addChangeListener( ChangeListener changeListener ) {
            sliderNode.addChangeListener( changeListener );
        }

        public void setValue( double v ) {
            sliderNode.setValue( v );
        }

        public void setRange( double min, double max ) {
            sliderNode.setRange( min, max );
        }

        public void setUnits( String units ) {
        }

        public void setPaintLabels( boolean b ) {
        }

        public void setPaintTicks( boolean b ) {
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