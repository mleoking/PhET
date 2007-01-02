package edu.colorado.phet.rotation.tests.combined;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:37:51 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.XYPlotFactory;
import edu.colorado.phet.rotation.graphs.combined.CombinedControlGraph;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;

public class TestCombinedGraph {
    private JFrame frame;

    public TestCombinedGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        XYPlotFactory factory = new XYPlotFactory();
        CombinedControlGraph combinedControlGraph = new CombinedControlGraph( phetPCanvas,
                                                                              new XYPlot[]{
                                                                                      factory.createXYPlot( "graph a", "range a" ),
                                                                                      factory.createXYPlot( "graph b", "range b" )
                                                                              } );
        phetPCanvas.addScreenChild( combinedControlGraph );
        combinedControlGraph.setBounds( 0, 0, 700, 500 );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestCombinedGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
