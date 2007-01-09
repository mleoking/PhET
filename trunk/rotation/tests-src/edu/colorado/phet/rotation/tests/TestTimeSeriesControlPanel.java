package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:50:48 PM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

import edu.colorado.phet.rotation.graphs.TimeSeriesControlPanel;
import edu.colorado.phet.rotation.graphs.TimeSeriesModel;

import javax.swing.*;

public class TestTimeSeriesControlPanel {
    private JFrame frame;

    public TestTimeSeriesControlPanel() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( new TimeSeriesControlPanel( new TimeSeriesModel() ) );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesControlPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
