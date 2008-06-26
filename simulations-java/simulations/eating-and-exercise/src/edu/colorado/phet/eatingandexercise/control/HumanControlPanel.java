package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.control.valuenode.LinearValueControlNode;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.util.FeetInchesFormat;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private EatingAndExerciseModel model;
    private Human human;
    private HumanSlider bodyFat;
    //    private LinearValueControl[] hs;
    private ArrayList listeners = new ArrayList();
    private HumanSlider ageSlider;

    public HumanControlPanel( final EatingAndExerciseModel model, final Human human ) {
        this.model = model;
        this.human = human;
        getGridBagConstraints().insets = new Insets( 4, 4, 4, 4 );
        setFillNone();

        add( new GenderControl( human ) );
        setFillHorizontal();

        ageSlider = new HumanSlider( 0, 100, EatingAndExerciseUnits.secondsToYears( human.getAge() ), EatingAndExerciseResources.getString( "age" ), EatingAndExerciseStrings.AGE_FORMAT.toPattern(), EatingAndExerciseResources.getString( "units.years" ) );
        add( ageSlider );

        ageSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setAge( EatingAndExerciseUnits.yearsToSeconds( ageSlider.getValue() ) );
            }
        } );

        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                ageSlider.setValue( EatingAndExerciseUnits.secondsToYears( human.getAge() ) );
            }
        } );

        //todo: find a more elegant way to decide when to reset the chart regions
        ageSlider.getTextField().addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getTextField().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getTextField().addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getSlider().addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );

        //todo: factor out slider that accommodates units
        final double minHeight = 1;
        final double maxHeight = 2.72;
        final HumanSlider heightControl = new HumanSlider( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ), model.getUnits().modelToViewDistance( human.getHeight() ), EatingAndExerciseResources.getString( "height" ), "0.00", model.getUnits().getDistanceUnit() );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setHeight( model.getUnits().viewToModelDistance( heightControl.getValue() ) );
            }
        } );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                double origHeight = human.getHeight();
                double value = model.getUnits().modelToViewDistance( human.getHeight() );

                //have to change range before changing value
                heightControl.setRange( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ) );
                heightControl.setValue( value );
                heightControl.setUnits( model.getUnits().getDistanceUnit() );

                heightControl.setPaintLabels( false );
                heightControl.setPaintTicks( false );

                human.setHeight( origHeight );//restore original value since clamping the range at a different time as the value can lead to incorrect values
            }
        } );
        heightControl.setTextFieldFormat( new FeetInchesFormat() );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                heightControl.setTextFieldFormat( model.getUnits() == EatingAndExerciseModel.Units.METRIC ? (NumberFormat) EatingAndExerciseStrings.AGE_FORMAT : new FeetInchesFormat() );
            }
        } );

        add( heightControl );


        final double minWeight = 0;
        final double maxWeight = EatingAndExerciseUnits.poundsToKg( 300 );
        final HumanSlider weightSlider = new HumanSlider( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ), model.getUnits().modelToViewMass( human.getMass() ), EatingAndExerciseResources.getString( "weight" ), EatingAndExerciseStrings.WEIGHT_FORMAT.toPattern(), model.getUnits().getMassUnit() );
        weightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( model.getUnits().viewToModelMass( weightSlider.getValue() ) );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                weightSlider.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
            }
        } );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                weightSlider.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
                weightSlider.setUnits( model.getUnits().getMassUnit() );
                weightSlider.setRange( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ) );
                weightSlider.setPaintLabels( false );
                weightSlider.setPaintTicks( false );
            }
        } );
        add( weightSlider );

        bodyFat = new HumanSlider( 0, 100, human.getFatMassPercent(), EatingAndExerciseResources.getString( "body.fat" ), "0.0", "%" );
        bodyFat.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setFatMassPercent( bodyFat.getValue() );
            }
        } );
        bodyFat.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                double va = human.getFatMassPercent();
                bodyFat.setValue( human.getFatMassPercent() + 1 );
                bodyFat.setValue( va );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                bodyFat.setValue( human.getFatMassPercent() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void genderChanged() {
                updateBodyFatSlider();
            }
        } );
        add( bodyFat );

//        hs = new LinearValueControl[]{age, heightControl, weightSlider, bmiSlider, bodyFat};
//        hs = new LinearValueControl[]{ageSlider, heightControl, weightSlider, bodyFat};
//        new AlignedSliderSetLayoutStrategy( hs ).doLayout();

        updateBodyFatSlider();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateBodyFatSlider();
            }
        } );
//        model.addListener( new EatingAndExerciseModel.Adapter() {
//            public void unitsChanged() {
//                new AlignedSliderSetLayoutStrategy( hs ).doLayout();
//            }
//        } );
    }

    public double getAgeSliderY() {
        return ageSlider.getY();
    }

    private void updateBodyFatSlider() {
        bodyFat.setRange( 0, human.getGender().getMaxFatMassPercent() );
        Hashtable table = new Hashtable();
        table.put( new Double( 10 ), new JLabel( EatingAndExerciseResources.getString( "muscular" ) ) );
        table.put( new Double( human.getGender().getMaxFatMassPercent() ), new JLabel( EatingAndExerciseResources.getString( "non-muscular" ) ) );
        bodyFat.setTickLabels( table );
//        new AlignedSliderSetLayoutStrategy( hs ).doLayout();
    }

    public static class HumanSlider extends JPanel {
        private LinearValueControlNode linearValueControlNode;

        public HumanSlider( double min, double max, double value, String label, String textFieldPattern, String units ) {
            PhetPCanvas canvas = new PhetPCanvas();
            linearValueControlNode = new LinearValueControlNode( label, units, min, max, value, new DefaultDecimalFormat( textFieldPattern ) );
            canvas.addScreenChild( linearValueControlNode );
            canvas.setPreferredSize( new Dimension( (int) linearValueControlNode.getFullBounds().getWidth(), (int) linearValueControlNode.getFullBounds().getHeight() ) );
            add( canvas );
//            setPreferredSize( canvas.getPreferredSize() );
        }

        public double getValue() {
            return linearValueControlNode.getValue();
        }

        public void addChangeListener( final ChangeListener changeListener ) {
            linearValueControlNode.addListener( new LinearValueControlNode.Listener() {
                public void valueChanged( double value ) {
                    changeListener.stateChanged( null );
                }
            } );
        }

        public void setValue( double v ) {
            linearValueControlNode.setValue( v );
        }

        public JTextField getTextField() {
            return new JTextField();
        }

        public JSlider getSlider() {
            return new JSlider();
        }

        public void setRange( double min, double max ) {
//            linearValueControlNode.setSliderRange( min, max );
        }

        public void setUnits( String distanceUnit ) {
            linearValueControlNode.setUnits( distanceUnit );
        }

        public void setPaintLabels( boolean b ) {
        }

        public void setPaintTicks( boolean b ) {
        }

        public void setTextFieldFormat( NumberFormat numberFormat ) {
        }

        public void setTickLabels( Hashtable table ) {
        }
    }

//    public static final class HumanSlider extends LinearValueControl {
//        public HumanSlider( double min, double max, double value, String label, String textFieldPattern, String units ) {
//            super( min, max, value, label, textFieldPattern, units, new DefaultLayoutStrategy() );
//            setColumns( 4 );
//            setPaintTicks( false );
//            setPaintLabels( false );
//            setSignifyOutOfBounds( false );
//        }
//
//        /*
//        * Don't clamp allowed value to slider range.
//         */
//        protected boolean isValueInRange( double value ) {
//            return true;
//        }
//
//        public void setColumns( int i ) {
//            getTextField().setColumns( i );
//        }
//
//        public void setPaintLabels( boolean b ) {
//            getSlider().setPaintLabels( b );
//        }
//
//        public void setPaintTicks( boolean b ) {
//            getSlider().setPaintTicks( b );
//        }
//
//        public void setTextFieldFormat( NumberFormat format ) {
//            super.setTextFieldFormat( format );
//        }
//    }

    private class GenderControl extends JPanel {
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

    public static interface Listener {
        void ageManuallyChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyAgeManuallyChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).ageManuallyChanged();
        }
    }
}