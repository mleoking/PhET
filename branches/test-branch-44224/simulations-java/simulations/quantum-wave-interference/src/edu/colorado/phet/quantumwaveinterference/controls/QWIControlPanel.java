/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 */

public class QWIControlPanel extends ControlPanel {
    private QWIModule module;

    private InitialConditionPanel initialConditionPanel;
    private AdvancedPanel advancedPanel;

    public QWIControlPanel( final QWIModule module ) {
        this.module = module;
        getContentPanel().setAnchor( GridBagConstraints.WEST );
        this.initialConditionPanel = createInitialConditionPanel();
        AdvancedPanel advancedICPanel = new AdvancedPanel( QWIResources.getString( "show" ), QWIResources.getString( "hide" ) );
        advancedICPanel.addControlFullWidth( this.initialConditionPanel );
        advancedICPanel.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "initial.conditions" ) ) );
        advancedICPanel.addListener( new AdvancedPanel.Listener() {
            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( QWIControlPanel.this );
                parent.invalidate();
                parent.validate();
                parent.repaint();
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
            }
        } );

        JButton fireParticle = new JButton( QWIResources.getString( "fire.particle" ) );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
        try {
            addMeasuringTools();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        advancedPanel = new AdvancedPanel( QWIResources.getString( "advanced" ), QWIResources.getString( "hide.advanced" ) );

        VerticalLayoutPanel intensityScreen = new IntensityScreenPanel( this );
        AdvancedPanel advancedIntensityScreen = new AdvancedPanel( QWIResources.getString( "screen1" ), QWIResources.getString( "screen2" ) );
        advancedIntensityScreen.addControl( intensityScreen );
        advancedPanel.addControlFullWidth( advancedIntensityScreen );

        VerticalLayoutPanel simulationPanel = createSimulationPanel( module );
        AdvancedPanel advSim = new AdvancedPanel( QWIResources.getString( "simulation" ), QWIResources.getString( "hide.simulation" ) );
        advSim.addControlFullWidth( simulationPanel );
        advancedPanel.addControlFullWidth( advSim );

//        final JSlider speed = new JSlider( JSlider.HORIZONTAL, 0, 1000, (int)( 1000 * 0.1 ) );
//        speed.setBorder( BorderFactory.createTitledBorder( "Classical Wave speed" ) );
//        speed.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                double x = new ModelViewTransform1D( 0, 0.5, speed.getMinimum(), speed.getMaximum() ).viewToModel( speed.getValue() );
//                System.out.println( "x = " + x );
////                classicalPropagator2ndOrder.setSpeed( x );
//            }
//        } );
//        setPreferredWidth();
    }

    protected void addSpacer() {
        addBox( 10, 10 );
    }

    protected void addBox( int width, int height ) {
        Box box = new Box( BoxLayout.X_AXIS );
        box.setPreferredSize( new Dimension( width, height ) );
        box.setSize( width, height );
        addControl( box );
    }

    //Todo this is not idempotent
    protected void setPreferredWidth( int width ) {
        setPreferredSize( new Dimension( width, getPreferredSize().height ) );
        setSize( getPreferredSize() );
        Box box = new Box( BoxLayout.X_AXIS );
        box.setPreferredSize( new Dimension( width - 2, 2 ) );
        box.setSize( width - 2, 2 );
        addControlFullWidth( box );
    }

    protected void addMeasuringTools() throws IOException {
        getContentPanel().setFillNone();
        addControl( new RulerPanel( getSchrodingerPanel() ) );
        addControl( new StopwatchCheckBox( getSchrodingerPanel() ) );
    }

    private WaveSetup getWaveSetup() {
        return initialConditionPanel.getWaveSetup();
    }

    private InitialConditionPanel createInitialConditionPanel() {
        return new InitialConditionPanel( this );
    }

    private VerticalLayoutPanel createSimulationPanel( final QWIModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "simulation1" ) ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getGridWidth(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "menus.resolution" ) ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSize( val, val );
            }
        } );
        simulationPanel.addFullWidth( gridWidth );

        final JSpinner timeStep = new JSpinner( new SpinnerNumberModel( 0.8, 0, 2, 0.1 ) );
        timeStep.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "dt" ) ) );

        timeStep.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double t = ( (Number)timeStep.getValue() ).doubleValue();
                getDiscreteModel().setDeltaTime( t );
            }
        } );
        simulationPanel.addFullWidth( timeStep );
        return simulationPanel;
    }

    public QWIPanel getSchrodingerPanel() {
        return module.getSchrodingerPanel();
    }

    public void fireParticle() {
        WaveSetup waveSetup = getWaveSetup();
        module.fireParticle( waveSetup );
    }

    public QWIModel getDiscreteModel() {
        return module.getQWIModel();
    }

    public QWIModule getModule() {
        return module;
    }

    public AdvancedPanel getAdvancedPanel() {
        return advancedPanel;
    }

    public JCheckBox getSlitAbsorptionCheckbox() {
        final JCheckBox absorbtiveSlit = new JCheckBox( QWIResources.getString( "controls.slits.absorbing-barriers" ), getDiscreteModel().isBarrierAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setBarrierAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        return absorbtiveSlit;
    }

}
