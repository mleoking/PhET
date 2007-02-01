package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.jfreechart.tests.TestDynamicJFreeChartNodeTree;
import edu.colorado.phet.rotation.graphs.JFreeChartCursorNode;

/**
 * User: Sam Reid
 * Date: Jan 29, 2007
 * Time: 8:35:28 AM
 * Copyright (c) Jan 29, 2007 by Sam Reid
 */

public class TestJFreeChartCursorNode extends TestDynamicJFreeChartNodeTree {

    public TestJFreeChartCursorNode() {
        JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( getDynamicJFreeChartNode() );
        getPhetPCanvas().addScreenChild( jFreeChartCursorNode );
    }

    public static void main( String[] args ) {
        new TestJFreeChartCursorNode().start();
    }

}
