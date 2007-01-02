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
    private double width;
    private double height;

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

    public boolean setBounds( double x, double y, double width, double height ) {
        this.width = width;
        this.height = height;
        relayout();
        setOffset( x, y );
        return super.setBounds( x, y, width, height );
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
        double yOffset = 0;
        double yPad = 5;
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = getGraphComponent( i );
            graphComponent.setOffset( 0, yOffset );
            graphComponent.setAvailableBounds( width, height / graphComponents.size() );
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
