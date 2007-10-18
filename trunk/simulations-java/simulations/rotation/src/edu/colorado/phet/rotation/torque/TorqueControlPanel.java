package edu.colorado.phet.rotation.torque;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.ResetButton;
import edu.colorado.phet.rotation.controls.RulerButton;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:48:07 AM
 */
public class TorqueControlPanel extends JPanel {
    private TorqueModule torqueModule;
    public static final int MIN_BRAKE = 0;
    public static final int MAX_BRAKE = 3;

    public TorqueControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, final TorqueModule torqueModule ) {
        super( new GridBagLayout() );
        this.torqueModule = torqueModule;
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        setBorder( BorderFactory.createTitledBorder( "Controls" ) );
        GridBagConstraints sliderSetConstraints = new GridBagConstraints();
        sliderSetConstraints.gridx = 0;
        sliderSetConstraints.gridy = GridBagConstraints.RELATIVE;
        JPanel sliderPanel = new JPanel( new GridBagLayout() );

        final RotationPlatform rp = torqueModule.getRotationModel().getRotationPlatform();


        final TorqueSlider outerRadiusSlider = new TorqueSlider( 0, RotationPlatform.MAX_RADIUS, rp.getRadius(), "R=Outer Radius", "0.00", "m" );
        outerRadiusSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rp.setRadius( outerRadiusSlider.getValue() );
                if ( outerRadiusSlider.getValue() < rp.getInnerRadius() ) {
                    rp.setInnerRadius( outerRadiusSlider.getValue() );
                }
            }
        } );
        rp.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                outerRadiusSlider.setValue( rp.getRadius() );
            }
        } );

        final TorqueSlider innerRadiusSlider = new TorqueSlider( 0, RotationPlatform.MAX_RADIUS, rp.getInnerRadius(), "r=Inner Radius", "0.00", "m" );
        innerRadiusSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rp.setInnerRadius( innerRadiusSlider.getValue() );
                if ( innerRadiusSlider.getValue() > rp.getRadius() ) {
                    rp.setRadius( innerRadiusSlider.getValue() );
                }
            }
        } );
        rp.addListener( new RotationPlatform.Adapter() {
            public void innerRadiusChanged() {
                innerRadiusSlider.setValue( rp.getInnerRadius() );
            }
        } );

        final TorqueSlider massSlider = new TorqueSlider( rp.getMass() / 10.0, rp.getMass() * 2, rp.getMass(), "Platform Mass", "0.00", "kg" );
        massSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rp.setMass( massSlider.getValue() );
            }
        } );
        rp.addListener( new RotationPlatform.Adapter() {
            public void massChanged() {
                massSlider.setValue( rp.getMass() );
            }
        } );

        final TorqueSlider frictionSlider = new TorqueSlider( MIN_BRAKE, MAX_BRAKE, torqueModule.getTorqueModel().getBrakeForceMagnitude(), "Force of Brake", "0.00", "N" );
        frictionSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                torqueModule.getTorqueModel().setBrakePressure( frictionSlider.getValue() );
            }
        } );
        torqueModule.getTorqueModel().addListener( new TorqueModel.Adapter() {
            public void brakePressureChanged() {
                frictionSlider.setValue( torqueModule.getTorqueModel().getBrakePressure() );
            }
        } );

        TorqueSlider[] sliders = new TorqueSlider[]{
                outerRadiusSlider,
                innerRadiusSlider,
                massSlider,
                frictionSlider
        };
        AlignedSliderSetLayoutStrategy alignedSliderSetLayoutStrategy = new AlignedSliderSetLayoutStrategy( sliders );
        alignedSliderSetLayoutStrategy.doLayout();
        for ( int i = 0; i < sliders.length; i++ ) {
            sliderPanel.add( sliders[i], sliderSetConstraints );
        }
        add( sliderPanel, getConstraints( 0, 0, 2 ) );
        add( graphSelectionControl, getConstraints( 1, 1, 1 ) );
        JPanel checkBoxPanel = new VerticalLayoutPanel();
        final JCheckBox showNonTangentialForces = new JCheckBox( "Allow non-tangential forces", torqueModule.getTorqueModel().isAllowNonTangentialForces() );
        showNonTangentialForces.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                torqueModule.getTorqueModel().setAllowNonTangentialForces( showNonTangentialForces.isSelected() );
            }
        } );
        checkBoxPanel.add( showNonTangentialForces );

        final JCheckBox showComponents = new JCheckBox( "Show Components", torqueModule.getTorqueModel().isShowComponents() );
        showComponents.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                torqueModule.getTorqueModel().setShowComponents( showComponents.isSelected() );
            }
        } );
        checkBoxPanel.add( showComponents );
        checkBoxPanel.add( new ResetButton( torqueModule ) );
        checkBoxPanel.add( new RulerButton( rulerNode ) );
        add( checkBoxPanel, getConstraints( 0, 1, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY, int gridWidth ) {
        return new GridBagConstraints( gridX, gridY, gridWidth, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }

    public static class TorqueSlider extends LinearValueControl {
        public TorqueSlider( double min, double max, double initialValue, String label, String textFieldPattern, String units ) {
            super( min, max, label, textFieldPattern, units, new NullLayoutStrategy() );
            setValue( initialValue );
            setMinorTickSpacing( ( max - min ) / 20.0 );
//            setMajorTickSpacing( ( max - min ) / 4.0 );
            setPaintTickLabels( false );
        }
    }

//    static class TorqueSliderLayout implements ILayoutStrategy {
//
//        public void doLayout( AbstractValueControl valueControl ) {
//
//            // Get the components that will be part of the layout
//            JComponent slider = valueControl.getSlider();
//            JComponent textField = valueControl.getTextField();
//            JComponent valueLabel = valueControl.getValueLabel();
//            JComponent unitsLabel = valueControl.getUnitsLabel();
//            slider.setLocation( 0, 0 );
//            textField.setLocation( slider.getWidth() + slider.getX(), 0 );
//            valueControl.add( slider );
//            valueControl.add( textField);
////            // Label+textfield+units in a panel.
////            JPanel valuePanel = new JPanel();
////            EasyGridBagLayout valueLayout = new EasyGridBagLayout( valuePanel );
////            valuePanel.setLayout( valueLayout );
////            valueLayout.setAnchor( GridBagConstraints.WEST );
////            valueLayout.addComponent( valueLabel, 0, 0 );
////            valueLayout.addComponent( textField, 0, 1 );
////            valueLayout.addComponent( unitsLabel, 0, 2 );
////
////            // Label+textfield+units above slider
////            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
////            valueControl.setLayout( layout );
////            layout.addComponent( valuePanel, 0, 0 );
////            layout.addComponent( slider, 0, 1 );
//        }
//    }
}
