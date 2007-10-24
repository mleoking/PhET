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
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:48:07 AM
 */
public class FullTorqueControlPanel extends JPanel {
    private AbstractTorqueModule torqueModule;
    public static final int MIN_BRAKE = 0;
    public static final int MAX_BRAKE = 3;

    public FullTorqueControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, final AbstractTorqueModule torqueModule, VectorViewModel vectorViewModel ) {
        super( new GridBagLayout() );
        this.torqueModule = torqueModule;

        setBorder( BorderFactory.createTitledBorder( "Controls" ) );
        GridBagConstraints sliderSetConstraints = new GridBagConstraints();
        sliderSetConstraints.gridx = 0;
        sliderSetConstraints.gridy = GridBagConstraints.RELATIVE;

        final RotationPlatform rp = torqueModule.getRotationModel().getRotationPlatform();

        TorqueSlider[] sliders = getSliders( torqueModule, rp );
        AlignedSliderSetLayoutStrategy alignedSliderSetLayoutStrategy = new AlignedSliderSetLayoutStrategy( sliders );
        alignedSliderSetLayoutStrategy.doLayout();
        JPanel sliderPanel = new JPanel( new GridBagLayout() );
        for ( int i = 0; i < sliders.length; i++ ) {
            sliderPanel.add( sliders[i], sliderSetConstraints );
        }
        add( sliderPanel, getConstraints( 0, 0, 2 ) );

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
        checkBoxPanel.add( new ShowVectorsControl( vectorViewModel ) );
        add( checkBoxPanel, getConstraints( 0, 1, 1 ) );
//        addGraphSelectionControl( rotationGraphSet, graphSetModel );
    }

    protected void addGraphSelectionControl( GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel ) {
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        add( graphSelectionControl, getConstraints( 1, 1, 1 ) );
    }

    protected TorqueSlider[] getSliders( AbstractTorqueModule torqueModule, RotationPlatform rp ) {
        return new TorqueSlider[]{
                createOuterRadiusSlider( rp ),
                createInnerRadiusSlider( rp ),
                createMassSlider( rp ),
                createFrictionSlider( torqueModule )
        };
    }

    protected TorqueSlider createFrictionSlider( final AbstractTorqueModule torqueModule ) {
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
        return frictionSlider;
    }

    protected TorqueSlider createMassSlider( final RotationPlatform rp ) {
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
        return massSlider;
    }

    protected TorqueSlider createInnerRadiusSlider( final RotationPlatform rp ) {
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
        return innerRadiusSlider;
    }

    protected TorqueSlider createOuterRadiusSlider( final RotationPlatform rp ) {
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
        return outerRadiusSlider;
    }

    private GridBagConstraints getConstraints( int gridX, int gridY, int gridWidth ) {
        return new GridBagConstraints( gridX, gridY, gridWidth, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }

    public static class TorqueSlider extends LinearValueControl {
        public TorqueSlider( double min, double max, double initialValue, String label, String textFieldPattern, String units ) {
            super( min, max, label, textFieldPattern, units, new NullLayoutStrategy() );
            setValue( initialValue );
            setMinorTickSpacing( ( max - min ) / 20.0 );
            setPaintTickLabels( false );
        }
    }
}
