// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:05:50 AM
 *
 */

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

public class TestControlGraph {
    private JFrame frame;
    private ControlGraph controlGraph;
    private PhetPCanvas phetPCanvas;

    public TestControlGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        ITemporalVariable v = new DefaultTemporalVariable();

        ControlGraphSeries graphSeries = new ControlGraphSeries( "series", Color.blue, "abbr", "units", null, v );

        phetPCanvas = new BufferedPhetPCanvas();
        controlGraph = new ControlGraph( phetPCanvas, graphSeries, "title", -10, 10, new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) ) );

        controlGraph.addValue( 0, 0 );
        controlGraph.addValue( 600, 10 );
        controlGraph.addValue( 800, -3 );
        phetPCanvas.addScreenChild( controlGraph );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        frame.setContentPane( phetPCanvas );
        relayout();
    }

    private void relayout() {
        controlGraph.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestControlGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
