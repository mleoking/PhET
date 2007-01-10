package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 1:15:36 PM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

import edu.colorado.phet.rotation.TimeSeriesGraphSetNode;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;

public class TestTimeSeriesGraphSetNode {
    private JFrame frame;

    public TestTimeSeriesGraphSetNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PSwingCanvas pSwingCanvas = new PSwingCanvas();
        frame.setContentPane( pSwingCanvas );
        pSwingCanvas.getLayer().addChild( new TimeSeriesGraphSetNode( pSwingCanvas, new GraphSetModel( new RotationGraphSet( pSwingCanvas, new RotationModel() ).getGraphSuite( 0 ) ), new TimeSeriesModel() ) );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesGraphSetNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
