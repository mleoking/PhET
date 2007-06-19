package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:40 PM
 *
 */

import edu.colorado.phet.common.motion.graphs.GraphTimeControlNode;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import javax.swing.*;

public class TestGraphControlNode {
    private JFrame frame;
    private SwingClock swingClock;

    public TestGraphControlNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new BufferedPhetPCanvas();
        swingClock = new SwingClock( 30, 1.0 );
        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), swingClock );

        swingClock.addClockListener( timeSeriesModel );
        phetPCanvas.addScreenChild( new GraphTimeControlNode( "title", "abbr", new SimulationVariable(), timeSeriesModel ) );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestGraphControlNode().start();
    }

    private void start() {
        frame.setVisible( true );
        swingClock.start();
    }
}
