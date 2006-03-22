/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.test;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.ClassicalWavePropagator;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.Potential;
import edu.colorado.phet.waveinterference.view.SimpleWavefunctionGraphic;
import edu.colorado.phet.waveinterference.view.TestColorMap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:57:27 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestWave {
    private JFrame frame;
    private Timer timer;
    private Lattice2D lattice2D;
    private ClassicalWavePropagator classicalWavePropagator;
    private SimpleWavefunctionGraphic simpleWavefunctionGraphic;

    public TestWave() {
        frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );
        lattice2D = new Lattice2D( 100, 80 );

        simpleWavefunctionGraphic = new SimpleWavefunctionGraphic( lattice2D, 10, 10, new TestColorMap( lattice2D ) );
        phetPCanvas.addScreenChild( simpleWavefunctionGraphic );
        frame.setSize( 600, 600 );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
        classicalWavePropagator = new ClassicalWavePropagator( new Potential() );
    }

    private void step() {
        double t = System.currentTimeMillis() / 1000.0;
        double period = 2;
        double frequency = 1 / period;
        for( int i = 20; i < 30; i++ ) {
            for( int j = 20; j < 30; j++ ) {
                double value = Math.cos( 2 * Math.PI * frequency * t );
//                System.out.println( "value = " + value );
                lattice2D.setValue( i, j, (float)value );
                classicalWavePropagator.setBoundaryCondition( i, j, (float)value );
            }
        }
        classicalWavePropagator.propagate( lattice2D );
//        lattice2D.printWaveToScreen( );
        simpleWavefunctionGraphic.update();
//        System.out.println( "--------------" );
    }

    public static void main( String[] args ) {
        new TestWave().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
