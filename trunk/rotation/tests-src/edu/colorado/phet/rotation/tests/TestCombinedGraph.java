package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:37:51 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.CombinedGraph;
import edu.colorado.phet.rotation.graphs.CombinedGraphComponent;

import javax.swing.*;

public class TestCombinedGraph {
    private JFrame frame;

    public TestCombinedGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        CombinedGraph combinedGraph = new CombinedGraph( new CombinedGraphComponent[]{
                new CombinedGraphComponent() {
                    public String getName() {
                        return "graph A";
                    }

                    public String getRangeAxisTitle() {
                        return "range for graph A";
                    }
                },
                new CombinedGraphComponent() {
                    public String getName() {
                        return "graph B";
                    }

                    public String getRangeAxisTitle() {
                        return "range for graph B";
                    }
                }

        } );
        phetPCanvas.addScreenChild( combinedGraph );
        combinedGraph.setBounds( 0, 0, 700, 500 );
        frame.setContentPane( phetPCanvas );


    }

    public static void main( String[] args ) {
        new TestCombinedGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
