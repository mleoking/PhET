package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:50:48 PM
 *
 */

import edu.colorado.phet.common.timeseries.TimeSeriesControlPanel;
import edu.colorado.phet.common.timeseries.TimeSeriesModel2;

import javax.swing.*;

public class TestTimeSeriesControlPanel {
    private JFrame frame;

    public TestTimeSeriesControlPanel() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( new TimeSeriesControlPanel( new TimeSeriesModel2() ) );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesControlPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
