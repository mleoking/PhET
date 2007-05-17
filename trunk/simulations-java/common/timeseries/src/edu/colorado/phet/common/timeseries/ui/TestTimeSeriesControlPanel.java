package edu.colorado.phet.common.timeseries.ui;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:50:48 PM
 *
 */

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import javax.swing.*;

public class TestTimeSeriesControlPanel {
    private JFrame frame;
    private SwingClock clock = new SwingClock( 30, 1 );

    public TestTimeSeriesControlPanel() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), 1.0 );
        frame.setContentPane( new TimeSeriesControlPanel( timeSeriesModel ) );
        clock.addClockListener( timeSeriesModel );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesControlPanel().start();
    }

    private void start() {
        frame.setVisible( true );
        clock.start();
    }
}
