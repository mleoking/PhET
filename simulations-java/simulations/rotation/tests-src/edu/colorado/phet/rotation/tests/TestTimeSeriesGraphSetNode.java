package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 1:15:36 PM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.TimeSeriesGraphSetNode;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.timeseries.TimeSeriesModel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestTimeSeriesGraphSetNode {
    private JFrame frame;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PhetPCanvas pSwingCanvas;

    public TestTimeSeriesGraphSetNode() {
        frame = new JFrame();
        frame.setSize( 1024, 768 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        pSwingCanvas = new PhetPCanvas();
        frame.setContentPane( pSwingCanvas );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( pSwingCanvas, new GraphSetModel( new RotationGraphSet( pSwingCanvas, new RotationModel() ).getGraphSuite( 0 ) ), new TimeSeriesModel() );
        pSwingCanvas.getLayer().addChild( timeSeriesGraphSetNode );
        pSwingCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
    }

    private void relayout() {
        timeSeriesGraphSetNode.setBounds( 0, 0, pSwingCanvas.getWidth(), pSwingCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesGraphSetNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
