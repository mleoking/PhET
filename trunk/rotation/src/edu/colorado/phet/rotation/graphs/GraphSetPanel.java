package edu.colorado.phet.rotation.graphs;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:21:40 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphSetPanel {
    private GraphSuite graphSuite;
    private ArrayList listeners = new ArrayList();

    public GraphSetPanel( GraphSuite graphSuite ) {
        this.graphSuite = graphSuite;
    }

    public void setRotationGraphSuite( GraphSuite graphSuite ) {
        if( this.graphSuite != graphSuite ) {
            this.graphSuite = graphSuite;
            notifyListeners();
        }
    }

    public GraphSuite getRotationGraphSuite() {
        return graphSuite;
    }

    public static interface Listener {
        void graphSuiteChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.graphSuiteChanged();
        }
    }
}
