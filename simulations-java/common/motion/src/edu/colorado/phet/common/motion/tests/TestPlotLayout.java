package edu.colorado.phet.common.motion.tests;

/**
 * Author: Sam Reid
 * Jun 19, 2007, 2:00:18 PM
 */

import javax.swing.*;

public class TestPlotLayout {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );

    public TestPlotLayout() {
        JPanel contentPane = new JPanel();


        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );


    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestPlotLayout().start();
    }
}
