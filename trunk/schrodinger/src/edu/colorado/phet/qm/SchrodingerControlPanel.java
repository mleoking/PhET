/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.view.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;
    public ModelSlider xSlider;
    private ModelSlider ySlider;
    private ModelSlider pxSlider;
    private ModelSlider pySlider;
    private ModelSlider aSlider;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
//        addControl( reset );

        VerticalLayoutPanel particleLauncher = new VerticalLayoutPanel();
        particleLauncher.setBorder( BorderFactory.createTitledBorder( "Particle Launcher" ) );

        xSlider = new ModelSlider( "X0", "1/L", 0, 1, 0.9 );
        ySlider = new ModelSlider( "Y0", "1/L", 0, 1, 0.5 );
        pxSlider = new ModelSlider( "Momentum-x0", "", -1, 1, -.2 );
        pySlider = new ModelSlider( "Momentum-y0", "", -1, 1, 0 );
        aSlider = new ModelSlider( "Size0", "", 0.5, 3, 1 );
//        addControlFullWidth( positionSlider );

        JButton fireParticle = new JButton( "Create Particle" );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );

        particleLauncher.add( xSlider );
        particleLauncher.add( ySlider );
        particleLauncher.add( pxSlider );
        particleLauncher.add( pySlider );
        particleLauncher.add( aSlider );
        particleLauncher.add( fireParticle );
        addControlFullWidth( particleLauncher );

        VerticalLayoutPanel colorPanel = createColorPanel( module );
        addControlFullWidth( colorPanel );

        VerticalLayoutPanel simulationPanel = getSimulationPanel( module );
        addControlFullWidth( simulationPanel );

        VerticalLayoutPanel potentialPanel = createPotentialPanel( module );
        addControlFullWidth( potentialPanel );
    }

    private VerticalLayoutPanel createPotentialPanel( SchrodingerModule module ) {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Potential" ) );

        JButton none = new JButton( "None" );
        none.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setPotential( new ConstantPotential( 0 ) );
            }
        } );
        layoutPanel.add( none );

        JButton doubleSlit = new JButton( "Double Slit" );
        doubleSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                setPotential( new VerticalSlitSet( ) );
                setPotential( createDoubleSlit() );
            }
        } );
        layoutPanel.add( doubleSlit );

        return layoutPanel;
    }

    private Potential createDoubleSlit() {
        Potential doubleSlit = new DoubleSlit().createDoubleSlit( getDiscreteModel().getXMesh(), getDiscreteModel().getYMesh(),
                                                                  (int)( getDiscreteModel().getXMesh() * 0.45 ), 5, 5, 10, 20000 );
        return doubleSlit;
    }

    private void setPotential( Potential potential ) {
        getModule().getDiscreteModel().setPotential( potential );
    }

    private VerticalLayoutPanel getSimulationPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( "Simulation" ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getXMesh(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( "discreteness" ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSpacing( val, val );
                setPotential( new ConstantPotential( 0.0 ) );
            }
        } );
        simulationPanel.addFullWidth( gridWidth );

        final double origDT = getDiscreteModel().getDeltaTime();
        System.out.println( "origDT = " + origDT );
        final JSpinner timeStep = new JSpinner( new SpinnerNumberModel( 4, 2.5, 4.5, 0.1 ) );
        timeStep.setBorder( BorderFactory.createTitledBorder( "DT^-1" ) );

        timeStep.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double x = ( (Number)timeStep.getValue() ).doubleValue();
                double exp = -1 - x;
                double t = Math.pow( 10, exp );
                System.out.println( "t = " + t );

                getDiscreteModel().setDeltaTime( t );
            }
        } );
        simulationPanel.addFullWidth( timeStep );
        return simulationPanel;
    }

    private VerticalLayoutPanel createColorPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel colorPanel = new VerticalLayoutPanel();
        colorPanel.setBorder( BorderFactory.createTitledBorder( "Colorize" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton blackBackground = new JRadioButton( "Default/Black" );
        buttonGroup.add( blackBackground );
        blackBackground.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().setWavefunctionColorMap( new DefaultColorMap( getModule() ) );
            }
        } );
        colorPanel.addFullWidth( blackBackground );
        blackBackground.setSelected( true );

        JRadioButton whiteBackground = new JRadioButton( "Default/White" );
        whiteBackground.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().setWavefunctionColorMap( new DefaultWhiteColorMap( getModule() ) );
            }
        } );
        buttonGroup.add( whiteBackground );
        colorPanel.addFullWidth( whiteBackground );

        JRadioButton grayMag = new JRadioButton( "Magnitude-Gray" );
        grayMag.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().setWavefunctionColorMap( new MagnitudeInGrayscale( getModule() ) );
            }
        } );
        colorPanel.addFullWidth( grayMag );
        buttonGroup.add( grayMag );

        JRadioButton visualTM = new JRadioButton( "Visual(tm)" );
        visualTM.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().setWavefunctionColorMap( new VisualColorMap( getModule() ) );
            }
        } );
        colorPanel.addFullWidth( visualTM );
        buttonGroup.add( visualTM );


        return colorPanel;
    }

    private SchrodingerPanel getModule() {
        return module.getSchrodingerPanel();
    }

    private void fireParticle() {
        //add the specified wavefunction everywhere, then renormalize..?
        //clear the old wavefunction.

        double x = getStartX();
        double y = getStartY();
        double px = getStartPx();
        double py = getStartPy();
        double a = getStartA();
        InitialWavefunction initialWavefunction = new GaussianWave( new Point( (int)x, (int)y ),
                                                                    new Vector2D.Double( px, py ), a );
        module.fireParticle( initialWavefunction );
    }

    private double getStartA() {
        double val = aSlider.getValue();
        double f = Math.pow( 10, -val );
        System.out.println( "val = " + val + ", f=" + f );
        return f;
    }

    private double getStartPy() {
        return pySlider.getValue() * 2;
    }

    private double getStartPx() {
        return pxSlider.getValue() * 2;
    }

    private double getStartY() {
        return ySlider.getValue() * getDiscreteModel().getYMesh();
    }

    private double getStartX() {
        return xSlider.getValue() * getDiscreteModel().getXMesh();
    }

    private DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }
}
