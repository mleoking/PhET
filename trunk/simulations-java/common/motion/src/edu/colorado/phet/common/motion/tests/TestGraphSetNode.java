package edu.colorado.phet.common.motion.tests;

/**
 * Author: Sam Reid
 * Jun 19, 2007, 2:00:18 PM
 */

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.DefaultSimulationVariable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestGraphSetNode {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );
    private PhetPCanvas phetPCanvas;
    private GraphSetNode graphSetNode;

    public TestGraphSetNode() {
        phetPCanvas = new BufferedPhetPCanvas();


        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );

        TimeSeriesModel timeSeriesModel = new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );
        MinimizableControlGraph minimizableControlGraphA = new MinimizableControlGraph( "labelA", new ControlGraph(
                phetPCanvas, new DefaultSimulationVariable(), "titleA", 0, 10, timeSeriesModel ) );
        MinimizableControlGraph minimizableControlGraphB = new MinimizableControlGraph( "Long labelB", new ControlGraph(
                phetPCanvas, new DefaultSimulationVariable(), "Long titleB", 0, 10, timeSeriesModel ) );


        graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{minimizableControlGraphA, minimizableControlGraphB} ) ) );
        graphSetNode.setAlignedLayout();
        phetPCanvas.addScreenChild( graphSetNode );

        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    private void relayout() {
        graphSetNode.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestGraphSetNode().start();
    }
}
