// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:21:40 AM
 */

public class GraphSetModel {
    private GraphSuite graphSuite;
    private ArrayList listeners = new ArrayList();

    public GraphSetModel( GraphSuite graphSuite ) {
        this.graphSuite = graphSuite;
    }

    public void setGraphSuite( GraphSuite graphSuite ) {
        //todo can't check for same state because of radio button listeners.
        this.graphSuite = graphSuite;
        notifyListeners();
    }

    public GraphSuite getGraphSuite() {
        return graphSuite;
    }

    public static interface Listener {
        void graphSuiteChanged();
    }

    public void addListener( GraphSetModel.Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            GraphSetModel.Listener listener = (GraphSetModel.Listener) listeners.get( i );
            listener.graphSuiteChanged();
        }
    }
}
