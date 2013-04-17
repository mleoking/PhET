// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;

import java.awt.*;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

public class TestDynamicChart extends JFrame {

    public TestDynamicChart() throws HeadlessException {

        super( " TestDynamicChart" );

        JPanel panel = new JPanel( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        final TestCanvas canvas = new TestCanvas( new Dimension( 800, 600 ) );
        panel.add( canvas, constraints );

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.gridwidth = 1;

        panel.add( new JButton( "Test A" ), constraints );

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;

        panel.add( new JButton( "Test B" ), constraints );

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;

        panel.add( new JButton( "Test C" ), constraints );

        setContentPane( panel );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        ( new Thread() {
            @Override
            public void run() {
                while ( true ) {
                    try {
                        Thread.sleep( 1000 / 25 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    canvas.addDataPoint();
                }
            }
        } ).start();
    }

    public static void main( String[] args ) {
        TestDynamicChart demo = new TestDynamicChart();
        demo.pack();
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );
    }


}
