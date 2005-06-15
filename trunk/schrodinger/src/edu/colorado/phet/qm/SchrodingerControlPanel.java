/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.potentials.DoubleSlit;
import edu.colorado.phet.qm.model.potentials.SimpleGradientPotential;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.*;

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
        addControl( reset );
        JPanel particleLauncher = createParticleLauncherPanel();
        AdvancedPanel advancedIC = new AdvancedPanel( "Show>>", "Hide<<" );
        advancedIC.addControlFullWidth( particleLauncher );
        advancedIC.setBorder( BorderFactory.createTitledBorder( "Initial Conditions" ) );
        advancedIC.addListener( new AdvancedPanel.Listener() {

            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( SchrodingerControlPanel.this );
                parent.invalidate();
                parent.validate();
                parent.repaint();
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
            }
        } );

        addControlFullWidth( advancedIC );

        JButton fireParticle = new JButton( "Create Particle" );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
        addControl( fireParticle );

        VerticalLayoutPanel colorPanel = createVisualizationPanel();
        addControlFullWidth( colorPanel );

        VerticalLayoutPanel simulationPanel = createSimulationPanel( module );
        addControlFullWidth( simulationPanel );

        VerticalLayoutPanel potentialPanel = createPotentialPanel( module );
        addControlFullWidth( potentialPanel );

        VerticalLayoutPanel exp = createExpectationPanel();
        addControlFullWidth( exp );


        JButton collapse = new JButton( "Collapse" );
        collapse.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                collapse();
            }
        } );
        addControl( collapse );

        VerticalLayoutPanel interactionPanel = createDetectorPanel();
        addControlFullWidth( interactionPanel );

        VerticalLayoutPanel boundaryPanel = createBoundaryPanel();
        addControlFullWidth( boundaryPanel );
    }

    private VerticalLayoutPanel createBoundaryPanel() {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Boundary Condition" ) );
        final JCheckBox planeWaveCheckbox = new JCheckBox( "Plane Wave" );

//        final PlaneWave planeWave = new PlaneWave( 10 * Math.PI, getDiscreteModel().getXMesh() );
        final PlaneWave planeWave = new PlaneWave( 40 * Math.PI, getDiscreteModel().getXMesh() );
        planeWave.setScale( 0.1 );
        int insetY = 0;
        int width = 3;
        final Rectangle rectangle = new Rectangle( getDiscreteModel().getXMesh() - width, insetY, width, getDiscreteModel().getYMesh() - insetY * 2 );
        final WaveSource waveSource = new WaveSource( rectangle, planeWave );
        waveSource.setNorm( 5.0 );

        planeWaveCheckbox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( planeWaveCheckbox.isSelected() ) {
                    getDiscreteModel().addListener( waveSource );
                }
                else {
                    getDiscreteModel().removeListener( waveSource );
                }
            }
        } );
        layoutPanel.add( planeWaveCheckbox );
        return layoutPanel;
    }

    private VerticalLayoutPanel createDetectorPanel() {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Detection" ) );
        JButton newDetector = new JButton( "Add Detector" );
        newDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addDetector();
            }
        } );
        layoutPanel.add( newDetector );

        final JCheckBox causeCollapse = new JCheckBox( "Causes Collapse", true );
        causeCollapse.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getDiscreteModel().setDetectionCausesCollapse( causeCollapse.isSelected() );
            }
        } );
        layoutPanel.add( causeCollapse );

        return layoutPanel;
    }

    private void collapse() {
        module.collapse();
    }

    private VerticalLayoutPanel createExpectationPanel() {
        VerticalLayoutPanel lay = new VerticalLayoutPanel();
        lay.setBorder( BorderFactory.createTitledBorder( "Observables" ) );
        final JCheckBox x = new JCheckBox( "<X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setDisplayXExpectation( x.isSelected() );
            }
        } );
        lay.add( x );

        final JCheckBox y = new JCheckBox( "<Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setDisplayYExpectation( y.isSelected() );
            }
        } );
        lay.add( y );

        final JCheckBox c = new JCheckBox( "collapse-to" );
        c.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setDisplayCollapsePoint( c.isSelected() );
            }
        } );
        lay.add( c );

        return lay;
    }

    private JPanel createParticleLauncherPanel() {
        VerticalLayoutPanel particleLauncher = new VerticalLayoutPanel();
//        particleLauncher.setBorder( BorderFactory.createTitledBorder( "Initial Conditions" ) );

        xSlider = new ModelSlider( "X0", "1/L", 0, 1, 0.8 );
        ySlider = new ModelSlider( "Y0", "1/L", 0, 1, 0.5 );
        pxSlider = new ModelSlider( "Momentum-x0", "", -1, 1, -.5 );
        pySlider = new ModelSlider( "Momentum-y0", "", -1, 1, 0 );
        aSlider = new ModelSlider( "Size0", "", 0.5, 3, 1 );
//        addControlFullWidth( positionSlider );

        particleLauncher.add( xSlider );
        particleLauncher.add( ySlider );
        particleLauncher.add( pxSlider );
        particleLauncher.add( pySlider );
        particleLauncher.add( aSlider );

        return particleLauncher;
    }

    private VerticalLayoutPanel createPotentialPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Potential" ) );

        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearPotential();
            }
        } );
        layoutPanel.add( clear );

        JButton doubleSlit = new JButton( "Add Double Slit" );
        doubleSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                setPotential( new VerticalSlitSet( ) );
                addPotential( createDoubleSlit() );
            }
        } );
        layoutPanel.add( doubleSlit );

        JButton slopingLeft = new JButton( "Add Slope" );
        slopingLeft.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addPotential( createSlopingPotential() );
            }
        } );
        layoutPanel.add( slopingLeft );

        JButton newBarrier = new JButton( "Add Barrier" );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        layoutPanel.add( newBarrier );

        return layoutPanel;
    }

    private void clearPotential() {
        module.getDiscreteModel().clearPotential();
        getSchrodingerPanel().clearPotential();
    }

    private Potential createSlopingPotential() {
        return new SimpleGradientPotential( 1000 );
    }

    private Potential createDoubleSlit() {
        Potential doubleSlit = new DoubleSlit().createDoubleSlit( getDiscreteModel().getXMesh(), getDiscreteModel().getYMesh(),
                                                                  (int)( getDiscreteModel().getXMesh() * 0.45 ), 5, 5, 10, 20000 );
        return doubleSlit;
    }

    private void addPotential( Potential potential ) {
        getSchrodingerPanel().getDiscreteModel().addPotential( potential );
    }

    private VerticalLayoutPanel createSimulationPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( "Simulation" ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getXMesh(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( "Resolution" ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSpacing( val, val );
                addPotential( new ConstantPotential( 0.0 ) );
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

    private VerticalLayoutPanel createVisualizationPanel() {
        VerticalLayoutPanel colorPanel = new VerticalLayoutPanel();
        colorPanel.setBorder( BorderFactory.createTitledBorder( "Color Scheme" ) );
        ButtonGroup buttonGroup = new ButtonGroup();


        JRadioButton visualTM = createVisualizationButton( "Rainbow", new VisualColorMap( getSchrodingerPanel() ), true, buttonGroup );
        colorPanel.addFullWidth( visualTM );

        JRadioButton grayMag = createVisualizationButton( "Magnitude-Gray", new MagnitudeInGrayscale( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( grayMag );

        JRadioButton realGray = createVisualizationButton( "Real-Gray", new RealGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( realGray );

        JRadioButton complexGray = createVisualizationButton( "Imaginary-Gray", new ImaginaryGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( complexGray );

        JRadioButton blackBackground = createVisualizationButton( "HSB on Black", new DefaultColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( blackBackground );

        JRadioButton whiteBackground = createVisualizationButton( "HSB on White", new DefaultWhiteColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( whiteBackground );
        return colorPanel;
    }

    private JRadioButton createVisualizationButton( String s, final ColorMap colorMap, boolean b, ButtonGroup buttonGroup ) {
        JRadioButton radioButton = new JRadioButton( s );
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setWavefunctionColorMap( colorMap );
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }

    private SchrodingerPanel getSchrodingerPanel() {
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
