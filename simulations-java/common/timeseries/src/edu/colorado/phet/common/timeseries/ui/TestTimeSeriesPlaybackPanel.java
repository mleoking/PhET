package edu.colorado.phet.common.timeseries.ui;

import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

import javax.swing.*;

public class TestTimeSeriesPlaybackPanel {
    private JFrame frame;

    public TestTimeSeriesPlaybackPanel() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( new TimeSeriesPlaybackPanel( new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new SwingClock( 30,1) ) ) );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesPlaybackPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
