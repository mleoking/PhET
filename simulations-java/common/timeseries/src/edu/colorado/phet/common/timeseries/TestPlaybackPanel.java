package edu.colorado.phet.common.timeseries;

import javax.swing.*;

public class TestPlaybackPanel {
    private JFrame frame;

    public TestPlaybackPanel() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( new TimeSeriesPlaybackPanel( new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), 1.0) ) );
    }

    public static void main( String[] args ) {
        new TestPlaybackPanel().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
