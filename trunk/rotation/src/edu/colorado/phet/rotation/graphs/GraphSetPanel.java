package edu.colorado.phet.rotation.graphs;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:21:40 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphSetPanel extends PNode {
    private GraphSetModel graphSetModel;
    private ArrayList graphComponents = new ArrayList();
    private GraphComponent.Listener graphComponentListener;

    public GraphSetPanel( GraphSetModel graphSetModel ) {
        this.graphSetModel = graphSetModel;
        graphComponentListener = new GraphComponent.Listener() {
            public void minimizeStateChanged() {
                relayout();
            }
        };
        graphSetModel.addListener( new GraphSetModel.Listener() {
            public void graphSuiteChanged() {
                updateGraphSuite();
            }
        } );
        updateGraphSuite();
    }

    private void updateGraphSuite() {
        while( graphComponents.size() > 0 ) {
            removeGraphComponent( 0 );
        }
        GraphSuite graphSuite = graphSetModel.getRotationGraphSuite();
        for( int i = 0; i < graphSuite.getGraphComponentCount(); i++ ) {
            addGraphComponent( graphSuite.getGraphComponent( i ) );
        }
        relayout();
    }

    private void addGraphComponent( GraphComponent graphComponent ) {
        graphComponents.add( graphComponent );
        addChild( graphComponent );
        graphComponent.addListener( graphComponentListener );
    }

    private void relayout() {
        double xOffset = 5;
        double yOffset = 5;
        double yPad = 5;
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = getGraphComponent( i );
            graphComponent.setOffset( xOffset, yOffset );
            yOffset += graphComponent.getFullBounds().getHeight() + yPad;
        }
    }

    private void removeGraphComponent( int i ) {
        GraphComponent graphComponent = (GraphComponent)graphComponents.remove( i );
        graphComponent.removeListener( graphComponentListener );
        removeChild( graphComponent );
    }

    private GraphComponent getGraphComponent( int i ) {
        return (GraphComponent)graphComponents.get( i );
    }

}
