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
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AlignedSliderSetLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.controls.ResetButton;
import edu.colorado.phet.rotation.controls.RulerButton;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:48:07 AM
 */
public class FullTorqueControlPanel extends VerticalLayoutPanel {
    private AbstractTorqueModule torqueModule;
    public static final int MIN_BRAKE = 0;
    public static final int MAX_BRAKE = 10;
    private JPanel leftPanel;

    public FullTorqueControlPanel( RulerNode rulerNode, final AbstractTorqueModule torqueModule, VectorViewModel vectorViewModel ) {
        this.torqueModule = torqueModule;

        setBorder( BorderFactory.createTitledBorder( RotationStrings.getString( "controls" ) ) );

        final RotationPlatform rp = torqueModule.getRotationModel().getRotationPlatform();

        TorqueSlider[] sliders = getSliders( torqueModule, rp );
        AlignedSliderSetLayoutStrategy alignedSliderSetLayoutStrategy = new AlignedSliderSetLayoutStrategy( sliders );
        alignedSliderSetLayoutStrategy.doLayout();
        JPanel sliderPanel = new VerticalLayoutPanel();
        for ( int i = 0; i < sliders.length; i++ ) {
            sliderPanel.add( sliders[i] );
        }
        add( sliderPanel );
        add( Box.createRigidArea( new Dimension( 10, 10 ) ) );
        HorizontalLayoutPanel controls = new HorizontalLayoutPanel();

//        JPanel rightPanel = new VerticalLayoutPanel();
        leftPanel = new VerticalLayoutPanel();
        final JCheckBox showNonTangentialForces = new JCheckBox( RotationStrings.getString( "controls.allow.non.tangential.forces" ), torqueModule.getTorqueModel().isAllowNonTangentialForces() );
        showNonTangentialForces.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                torqueModule.getTorqueModel().setAllowNonTangentialForces( showNonTangentialForces.isSelected() );
            }
        } );
//        rightPanel.add( showNonTangentialForces );

        final JCheckBox showComponents = new JCheckBox( RotationStrings.getString( "controls.show.components" ), torqueModule.getTorqueModel().isShowComponents() );
        showComponents.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                torqueModule.getTorqueModel().setShowComponents( showComponents.isSelected() );
            }
        } );
//        rightPanel.add( showComponents );
//        rightPanel.add( new ResetButton( torqueModule ) );
        leftPanel.add( new RulerButton( rulerNode ) );
        final ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );
        leftPanel.add( showVectorsControl );
        leftPanel.add( showComponents );
        leftPanel.add( showNonTangentialForces );
        leftPanel.add( new ResetButton( torqueModule ) );

        controls.add( leftPanel );
        add( Box.createRigidArea( new Dimension( 30, 30 ) ) );
//        controls.add( rightPanel );

        add( controls );
//        addGraphSelectionControl( rotationGraphSet, graphSetModel );
    }

    protected void addGraphSelectionControl( GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel ) {
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        leftPanel.add( graphSelectionControl );
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
        final TorqueSlider frictionSlider = new TorqueSlider( MIN_BRAKE, MAX_BRAKE, torqueModule.getTorqueModel().getBrakeForceMagnitude(), RotationStrings.getString( "variable.force.of.brake" ), "0.00", RotationStrings.getString( "units.n" ) );
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
        final TorqueSlider massSlider = new TorqueSlider( RotationPlatform.MIN_MASS, RotationPlatform.MAX_MASS, rp.getMass(), RotationStrings.getString( "variable.platform.mass" ), "0.00", RotationStrings.getString( "units.kg" ) );
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
        final TorqueSlider innerRadiusSlider = new TorqueSlider( 0, RotationPlatform.MAX_RADIUS, rp.getInnerRadius(), RotationStrings.getString( "variable.r.inner.radius" ), "0.00", RotationStrings.getString( "units.mm" ) );
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
        final TorqueSlider outerRadiusSlider = new TorqueSlider( 0, RotationPlatform.MAX_RADIUS, rp.getRadius(), RotationStrings.getString( "variable.r.outer.radius" ), "0.00", RotationStrings.getString( "units.mm" ) );
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

    public static class TorqueSlider extends LinearValueControl {
        public TorqueSlider( double min, double max, double initialValue, String label, String textFieldPattern, String units ) {
            super( min, max, label, textFieldPattern, units, new NullLayoutStrategy() );
            setValue( initialValue );
            setMinorTickSpacing( ( max - min ) / 20.0 );
            setPaintTickLabels( false );
            //make sure control panel is thin enough, since this impacts the layout of the rest of the sim
            getSlider().setPreferredSize( new Dimension( (int) (getSlider().getPreferredSize().width*0.7),getSlider().getPreferredSize().height) );
        }
    }
}
