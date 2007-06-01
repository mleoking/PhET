package edu.colorado.phet.energyskatepark.view;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 1:32:27 PM
 */

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

import javax.swing.*;

public class TestClockControlPanel {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );

    public TestClockControlPanel() {
        JPanel contentPane = new EnergySkateParkClockControlPanel( new SwingClock( 30, 1.0 ) );

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestClockControlPanel().start();
    }
}


