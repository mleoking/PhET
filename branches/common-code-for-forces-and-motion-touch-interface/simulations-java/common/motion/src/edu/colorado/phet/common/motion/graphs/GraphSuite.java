// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

/**
 * The GraphSuite represents a collection of MinimizableControlGraphs, so that the set of visible graphs can be easily selected and changed.
 *
 * @author Sam Reid
 */
public class GraphSuite {
    private MinimizableControlGraph[] minimizableControlGraphs;

    public GraphSuite( MinimizableControlGraph[] minimizableControlGraphs ) {
        this.minimizableControlGraphs = minimizableControlGraphs;
    }

    public MinimizableControlGraph getGraphComponent( int i ) {
        return minimizableControlGraphs[i];
    }

    public int getGraphComponentCount() {
        return minimizableControlGraphs.length;
    }

    public String getLabel() {
//        String str = "<html>";
        String str = "";
        for ( int i = 0; i < minimizableControlGraphs.length; i++ ) {
            MinimizableControlGraph minimizableControl = minimizableControlGraphs[i];
            str += minimizableControl.getLabel();
            if ( i < minimizableControlGraphs.length - 1 ) {
                str += ",";
            }
        }
        return str;
//        return str + "</html>";
    }
}
