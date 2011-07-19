// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.timeseries.ui;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 12:50:48 PM
 *
 */

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

public class TestTimeSeriesControlPanel {
    private JFrame frame;
    private ConstantDtClock clock = new ConstantDtClock( 30, 1 );

    public TestTimeSeriesControlPanel() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), clock );
        frame.setContentPane( new TimeSeriesControlPanel( timeSeriesModel, 0.1, 2.0 ) );
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
