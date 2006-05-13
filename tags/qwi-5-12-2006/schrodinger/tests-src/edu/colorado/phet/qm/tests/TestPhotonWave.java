/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.model.CylinderSource;
import edu.colorado.phet.qm.model.Damping;
import edu.colorado.phet.qm.model.WaveModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.qm.model.waves.FlatDampedWave;
import edu.colorado.phet.qm.model.waves.PlaneWave;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.complexcolormaps.GrayscaleColorMap;
import edu.colorado.phet.qm.view.piccolo.SimpleWavefunctionGraphic;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 14, 2006
 * Time: 8:37:03 PM
 * Copyright (c) Mar 14, 2006 by Sam Reid
 */

public class TestPhotonWave {
    private JFrame frame;
    private Timer timer;
    private ClassicalWavePropagator classicalWavePropagator;
    private Wavefunction wavefunction;
    private SimpleWavefunctionGraphic simpleWavefunctionGraphic;
    private WaveModel waveModel;
    private double dt = 1.0;

    public TestPhotonWave() {

        wavefunction = new Wavefunction( 100, 100 );
        classicalWavePropagator = new ClassicalWavePropagator( new ConstantPotential() );
        waveModel = new WaveModel( wavefunction, classicalWavePropagator );
        simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( wavefunction, 5, 5, new ComplexColorMapAdapter( wavefunction, new GrayscaleColorMap.Real() ) );
        PCanvas pCanvas = new PCanvas();
        pCanvas.getLayer().addChild( simpleWavefunctionGraphic );
        frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 800, 800 );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );

//        PlaneWave planeWave=new PlaneWave( 0.001,100);
        setMomentum( 1000.001 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JTextField textField = new JTextField();
        textField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String val = textField.getText();
                double v = Double.parseDouble( val );
                setMomentum( v );
            }
        } );
        JFrame controls = new JFrame();
        controls.setContentPane( textField );
        controls.setVisible( true );
    }

    double simulationTime = 0;

    private void setMomentum( double k ) {

    }

    protected Rectangle createRectRegionForCylinder() {
        return new Rectangle( 0, getWavefunction().getHeight() - 10,
                              getWavefunction().getWidth(), 10 );
    }

    private Wavefunction getWavefunction() {
        return wavefunction;
    }

    double k = 100;

    private void step() {
        classicalWavePropagator.propagate( wavefunction );
        new Damping().damp( wavefunction );
        simpleWavefunctionGraphic.update();
        PlaneWave planeWave = new PlaneWave( k, 100 );
        FlatDampedWave flatDampedWave = new FlatDampedWave( planeWave, 1.0, 100 );
        CylinderSource cylinderSource = new CylinderSource( createRectRegionForCylinder(), flatDampedWave );
        cylinderSource.initializeEntrantWave( waveModel, simulationTime );
        simulationTime += dt;
    }

    public static void main( String[] args ) {
        new TestPhotonWave().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
