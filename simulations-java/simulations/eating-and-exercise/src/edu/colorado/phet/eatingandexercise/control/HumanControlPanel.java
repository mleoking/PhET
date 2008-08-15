package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.util.FeetInchesFormat;
import edu.colorado.phet.eatingandexercise.util.YearMonthFormat;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private EatingAndExerciseModel model;
    private Human human;

    private ArrayList listeners = new ArrayList();

    private HumanSlider bodyFatSlider;
    private HumanSlider ageSlider;
    private HumanSlider heightSlider;
    private HumanSlider weightSlider;
    private final HumanSlider p0Slider;
    private final JLabel pheart;

    private ArrayList sliders = new ArrayList();

    public HumanControlPanel( EatingAndExerciseCanvas canvas, final EatingAndExerciseModel model, final Human human ) {
        this.model = model;
        this.human = human;
        setFillNone();

        add( new GenderControl( human ) );
        setFillHorizontal();

//        add( new ActivityLevelControlPanel( human ) );
        add( new ActivityLevelControl( canvas, human ) );

        ageSlider = new HumanSlider( 0, 100, EatingAndExerciseUnits.secondsToYears( human.getAge() ),
                                     EatingAndExerciseResources.getString( "age" ), EatingAndExerciseStrings.AGE_FORMAT.toPattern(),
//                                     EatingAndExerciseResources.getString( "units.years" )
                                     "" 
        );
        ageSlider.setTextFieldFormat( new YearMonthFormat() );
        sliders.add( ageSlider );
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

        ageSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updatePercentFat();
                notifyAgeManuallyChanged();
            }
        } );

        //todo: factor out slider that accommodates units
        final double minHeight = 1;
        final double maxHeight = 2.72;
        double origHeight = model.getUnits().modelToViewDistance( human.getHeight() );
//        System.out.println( "origHeight = " + origHeight );
        heightSlider = new HumanSlider( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ),
                                        origHeight, EatingAndExerciseResources.getString( "height" ), "0.00", model.getUnits().getDistanceUnit() );
        heightSlider.setTextFieldFormat( new FeetInchesFormat() );
        heightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setHeight( model.getUnits().viewToModelDistance( heightSlider.getValue() ) );
            }
        } );
        heightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updatePercentFat();
            }
        } );
        human.addListener( new Human.Adapter() {
            public void heightChanged() {
                heightSlider.setValue( model.getUnits().modelToViewDistance( human.getHeight() ) );
            }
        } );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                double origHeight = human.getHeight();
                double value = model.getUnits().modelToViewDistance( human.getHeight() );

                //have to change range before changing value
                heightSlider.setRange( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ) );
                heightSlider.setValue( value );
                heightSlider.setUnits( model.getUnits().getDistanceUnit() );

                heightSlider.setPaintLabels( false );
                heightSlider.setPaintTicks( false );

                human.setHeight( origHeight );//restore original value since clamping the range at a different time as the value can lead to incorrect values
            }
        } );
        heightSlider.setTextFieldFormat( new FeetInchesFormat() );//todo: does this need to be called twice?
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                heightSlider.setTextFieldFormat( model.getUnits() == EatingAndExerciseModel.Units.METRIC ? (NumberFormat) EatingAndExerciseStrings.AGE_FORMAT : new FeetInchesFormat() );
            }
        } );

        add( heightSlider );
        sliders.add( heightSlider );


        final double minWeight = 0;
        final double maxWeight = EatingAndExerciseUnits.poundsToKg( 300 );
        weightSlider = new HumanSlider( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ), model.getUnits().modelToViewMass( human.getMass() ), EatingAndExerciseResources.getString( "weight" ), EatingAndExerciseStrings.WEIGHT_FORMAT.toPattern(), model.getUnits().getMassUnit() );
        weightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( model.getUnits().viewToModelMass( weightSlider.getValue() ) );
            }
        } );
        weightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updatePercentFat();
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
        sliders.add( weightSlider );

//        JButton autoBodyFat = new JButton( "<html>Estimate Body Fat %</html>" );
//        autoBodyFat.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                human.setFatMassPercent( model.getUserSpecifiedBodyParameters().getPreferredFatMassPercent( human ) );
//            }
//        } );
//        setFillNone();
//        add( autoBodyFat );
//        setFillHorizontal();

        bodyFatSlider = new HumanSlider( 0, 100, human.getFatMassPercent(), EatingAndExerciseResources.getString( "body.fat" ), "0.0", "%" );
        bodyFatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setFatMassPercent( bodyFatSlider.getValue() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                bodyFatSlider.setValue( human.getFatMassPercent() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void genderChanged() {
                updateBodyFatSlider();
            }
        } );
        add( bodyFatSlider );
        sliders.add( bodyFatSlider );



        p0Slider = new HumanSlider( Math.min( 0, Human.Gender.P0 ), Math.max( 1 / 100.0, Human.Gender.P0 ), Human.Gender.P0, "p0", "0.0000", "units" );
        p0Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Human.Gender.P0 = p0Slider.getValue();
            }
        } );
//        add( p0Slider );

        pheart = new JLabel();
        p0Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateHeartAttackProbabilityLabel();
            }
        } );
        human.addListener( new Human.Adapter() {
            public void heartAttackProbabilityChanged() {
                updateHeartAttackProbabilityLabel();
            }
        } );
        updateHeartAttackProbabilityLabel();
//        add( pheart );

        //wire up listeners to these properties directly, since they are independent variables (ie won't be changed by the model)
        human.addListener( new Human.Adapter() {
            public void genderChanged() {
                updatePercentFat();
            }

            public void activityLevelChanged() {
                updatePercentFat();
            }
        } );
//        updatePercentFat();

        updateBodyFatSlider();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLayout();
            }
        } );
        updateLayout();
    }

    private void updatePercentFat() {
//        double percentFat = human.getNormativePercentFat();
//        human.setFatMassPercent( percentFat );
//        System.out.println( "HumanControlPanel.updatePercentFat based on user specified change" );
        human.setFatMassPercent( model.getUserSpecifiedBodyParameters().getPreferredFatMassPercent( human ) );
    }

    private void updateLayout() {
        updateBodyFatSlider();
        relayoutSliders();
    }

    private void relayoutSliders() {
        HumanSlider[] s = (HumanSlider[]) sliders.toArray( new HumanSlider[sliders.size()] );
        HumanSlider.layout( s );
    }

    private void updateHeartAttackProbabilityLabel() {
        pheart.setText( "probability of heart attack per day=" + new DecimalFormat( "0.0000000" ).format( human.getHeartAttackProbabilityPerDay() ) );
    }

    public double getAgeSliderY() {
        return ageSlider.getY();
    }

    private void updateBodyFatSlider() {
        bodyFatSlider.setRange( 0, human.getGender().getMaxFatMassPercent() );
        Hashtable table = new Hashtable();
        table.put( new Double( 10 ), new JLabel( EatingAndExerciseResources.getString( "muscular" ) ) );
        table.put( new Double( human.getGender().getMaxFatMassPercent() ), new JLabel( EatingAndExerciseResources.getString( "non-muscular" ) ) );
        bodyFatSlider.setTickLabels( table );
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