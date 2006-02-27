/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests.thirdparty.yalabik;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: Sam Reid
 * Date: Feb 25, 2006
 * Time: 6:16:50 PM
 * Copyright (c) Feb 25, 2006 by Sam Reid
 */

public class TD_QuantLauncher {
    private JFrame jframe;
//    this( 30, 256, 0.25, 0.5 );
    private int latticeSize = AbsorptionSimulation.DEFAULT_LATTICE_SIZE;
    private long loopDelay = AbsorptionSimulation.DEFAULT_DELAY;
    private Simulation oldSim;
    private double xk0 = AbsorptionSimulation.DEFAULT_XK0;
    private double dt = AbsorptionSimulation.DEFAULT_DT;

    public TD_QuantLauncher() {
        jframe = new JFrame( "Feasibility Test" );
        jframe.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        final JSpinner numPts = new JSpinner( new SpinnerNumberModel( latticeSize, 16, 2048, 16 ) );
        numPts.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                latticeSize = ( (Integer)numPts.getValue() ).intValue();
            }
        } );
        JLabel label = new JLabel( "Number of lattice points." );
        verticalLayoutPanel.add( label );
        verticalLayoutPanel.add( numPts );

        final JSpinner delay = new JSpinner( new SpinnerNumberModel( loopDelay, 0, 60, 1 ) );
        delay.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                loopDelay = ( (Number)delay.getValue() ).longValue();
            }
        } );
        verticalLayoutPanel.add( new JLabel( "Animation Delay(milliseconds)" ) );
        verticalLayoutPanel.add( delay );

        final JSpinner k0 = new JSpinner( new SpinnerNumberModel( xk0, 0, 1.0, 0.01 ) );
        k0.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                xk0 = ( (Number)k0.getValue() ).doubleValue();
            }
        } );
        verticalLayoutPanel.add( new JLabel( "xk0" ) );
        verticalLayoutPanel.add( k0 );

        final JSpinner dtSpinner = new JSpinner( new SpinnerNumberModel( dt, 0, 1, 0.01 ) );
        dtSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dt = ( (Number)dtSpinner.getValue() ).doubleValue();
            }
        } );
        verticalLayoutPanel.add( new JLabel( "dt" ) );
        verticalLayoutPanel.add( dtSpinner );

        JButton launch = new JButton( "Launch Simulation" );
        launch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                relaunch();
            }
        } );
        verticalLayoutPanel.add( launch );


        jframe.setContentPane( verticalLayoutPanel );
        jframe.pack();
    }

    public static void main( String[] args ) {
        new TD_QuantLauncher().start();
    }

    private void start() {
        jframe.setVisible( true );
    }

    private void relaunch() {
        if( oldSim != null ) {
            oldSim.stop();
            try {
                Thread.sleep( 100 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        oldSim = new Simulation();
        oldSim.start();
    }

    class Simulation {
        private JFrame frame;
        private AbsorptionSimulation app;

        public Simulation() {
            frame = new JFrame();
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    stop();
                }
            } );
//            AbsorptionSimulation.LOOP_DELAY = loopDelay;
//            AbsorptionSimulation.LATTICE_SIZE = latticeSize;
//            AbsorptionSimulation.XK0_VALUE = xk0;
//            AbsorptionSimulation.dt = dt;
            app = new AbsorptionSimulation( loopDelay, latticeSize, xk0, dt );

            frame.setContentPane( app );
            frame.pack();
        }

        void stop() {
            app.dispose();
            frame.dispose();
        }

        void start() {
            frame.pack();
            frame.setLocation( jframe.getX() + jframe.getWidth(), 0 );
            frame.setVisible( true );
        }
    }
}
