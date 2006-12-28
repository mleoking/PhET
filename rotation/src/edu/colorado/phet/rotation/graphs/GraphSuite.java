package edu.colorado.phet.rotation.graphs;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:30:07 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphSuite {
    private GraphComponent[] graphComponent;

    public GraphSuite( GraphComponent[] graphComponent ) {
        this.graphComponent = graphComponent;
    }

    public GraphComponent getGraphComponent( int i ) {
        return graphComponent[i];
    }

    public int getGraphComponentCount() {
        return graphComponent.length;
    }

    public String getLabel() {
        String str = "";
        for( int i = 0; i < graphComponent.length; i++ ) {
            GraphComponent component = graphComponent[i];
            str += component.getLabel();
            if( i < graphComponent.length - 1 ) {
                str += ",";
            }
        }
        return str;
    }
}
