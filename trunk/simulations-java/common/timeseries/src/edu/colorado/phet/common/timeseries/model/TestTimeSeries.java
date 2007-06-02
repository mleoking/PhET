package edu.colorado.phet.common.timeseries.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesPlaybackPanel;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 15, 2007, 9:27:10 PM
 */
public class TestTimeSeries {
    private JFrame frame;

    public TestTimeSeries() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        RecordableModel recordableModel = new MyRecordableModel();
        Clock clock = new SwingClock( 30, 1.0 );
        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        frame.setContentPane( new TimeSeriesPlaybackPanel( timeSeriesModel ) );

        clock.addClockListener( timeSeriesModel );
        clock.start();
    }

    public static void main( String[] args ) {
        new TestTimeSeries().start();
    }

    private void start() {
        frame.setVisible( true );
    }

    public static class MyRecordableModel implements RecordableModel {
        double time = 0;

        public void stepInTime( double simulationTimeChange ) {
            time += simulationTimeChange;
            System.out.println( "TestTimeSeries.step: time=" + time );
        }

        public Object getState() {
            System.out.println( "TestTimeSeries.getState: time=" + time );
            return new Double( time );
        }

        public void setState( Object o ) {
            this.time = ( (Double)o ).doubleValue();
            System.out.println( "TestTimeSeries.setState: time=" + time );
        }

        public void resetTime() {
            time = 0;
            System.out.println( "reset time = " + time );
        }
    }
}
