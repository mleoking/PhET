/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;

    private InitialConditionPanel initialConditionPanel;
    private AdvancedPanel advancedPanel;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;

        this.initialConditionPanel = createInitialConditionPanel();
        AdvancedPanel advancedICPanel = new AdvancedPanel( "Show>>", "Hide<<" );
        advancedICPanel.addControlFullWidth( this.initialConditionPanel );
        advancedICPanel.setBorder( BorderFactory.createTitledBorder( "Initial Conditions" ) );
        advancedICPanel.addListener( new AdvancedPanel.Listener() {
            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( SchrodingerControlPanel.this );
                parent.invalidate();
                parent.validate();
                parent.repaint();
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
            }
        } );

        JButton fireParticle = new JButton( "Fire Particle" );
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


        advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );

        VerticalLayoutPanel intensityScreen = new IntensityScreenPanel( this );
        AdvancedPanel advancedIntensityScreen = new AdvancedPanel( "Screen>>", "Screen<<" );
        advancedIntensityScreen.addControl( intensityScreen );
        advancedPanel.addControlFullWidth( advancedIntensityScreen );

        VerticalLayoutPanel simulationPanel = createSimulationPanel( module );
        AdvancedPanel advSim = new AdvancedPanel( "Simulation>>", "Hide Simulation<<" );
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

    }

    protected void addMeasuringTools() throws IOException {
        addRulerPanel();
        addStopwatchPanel();
    }

    protected void addStopwatchPanel() {
        final JCheckBox stopwatchCheckBox = new JCheckBox( "Stopwatch" );
        stopwatchCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setStopwatchVisible( stopwatchCheckBox.isSelected() );
            }
        } );
        new Timer( 500, new ActionListener() {//todo why does this drag the application if time < 30 ms?

            public void actionPerformed( ActionEvent e ) {
                stopwatchCheckBox.setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
        addControl( stopwatchCheckBox );
        addControl( new PropagatorPanel( getDiscreteModel() ) );
    }

    protected void addRulerPanel() throws IOException {
        RulerPanel rulerPanel = new RulerPanel( getSchrodingerPanel() );
        addControl( rulerPanel );
    }

    private WaveSetup getWaveSetup() {
        return initialConditionPanel.getWaveSetup();
    }


    private InitialConditionPanel createInitialConditionPanel() {
        return new InitialConditionPanel( this );
    }

    protected VerticalLayoutPanel createPotentialPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setFillNone();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Potential" ) );

        JButton clear = new JButton( "Remove All" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearPotential();
            }
        } );

        JButton newBarrier = new JButton( "Add Barrier" );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        layoutPanel.add( newBarrier );
        layoutPanel.add( clear );

        return layoutPanel;
    }

    private void clearPotential() {
        module.clearPotential();
    }

    private VerticalLayoutPanel createSimulationPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( "Simulation" ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getGridWidth(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( "Resolution" ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSize( val, val );
            }
        } );
        simulationPanel.addFullWidth( gridWidth );

        final JSpinner timeStep = new JSpinner( new SpinnerNumberModel( 0.8, 0, 2, 0.1 ) );
        timeStep.setBorder( BorderFactory.createTitledBorder( "DT" ) );

        timeStep.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double t = ( (Number)timeStep.getValue() ).doubleValue();
                getDiscreteModel().setDeltaTime( t );
            }
        } );
        simulationPanel.addFullWidth( timeStep );
        return simulationPanel;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return module.getSchrodingerPanel();
    }

    public void fireParticle() {
        WaveSetup waveSetup = getWaveSetup();
        module.fireParticle( waveSetup );
    }

    public DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }

    public SchrodingerModule getModule() {
        return module;
    }

    public AdvancedPanel getAdvancedPanel() {
        return advancedPanel;
    }

    public JCheckBox getSlitAbsorptionCheckbox() {
        final JCheckBox absorbtiveSlit = new JCheckBox( "Absorbing Barriers", getDiscreteModel().isBarrierAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setBarrierAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        return absorbtiveSlit;
    }

}
