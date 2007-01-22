package edu.colorado.phet.rotation.graphs;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:21:40 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphSetNode extends PNode {
    private GraphSetModel graphSetModel;
    private ArrayList graphComponents = new ArrayList();
    private GraphComponent.Listener graphComponentListener;
    private double width;
    private double height;

    public GraphSetNode( GraphSetModel graphSetModel ) {
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

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        this.width = width;
        this.height = height;
        relayout();
        setOffset( x, y );
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
        double yPad = 5;
        double availableY = height;
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            availableY -= graphComponent.getFixedHeight();
            availableY -= yPad;
        }

        double yOffset = 0;
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = getGraphComponent( i );
            graphComponent.setOffset( 0, yOffset );
            if( numMaximized() > 0 ) {
                graphComponent.setAvailableBounds( width, availableY / numMaximized() );
            }
            yOffset += graphComponent.getFullBounds().getHeight() + yPad;
        }
    }

    private int numMaximized() {
        int count = 0;
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            count += graphComponent.isMinimized() ? 0 : 1;
        }
        return count;
    }

    private void removeGraphComponent( int i ) {
        GraphComponent graphComponent = (GraphComponent)graphComponents.remove( i );
        graphComponent.removeListener( graphComponentListener );
        removeChild( graphComponent );
    }

    private GraphComponent getGraphComponent( int i ) {
        return (GraphComponent)graphComponents.get( i );
    }

    public void setFlowLayout() {
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            graphComponent.setFlowLayout();
        }
    }

    public void setAlignedLayout() {
        GraphComponent[] gc = (GraphComponent[])graphComponents.toArray( new GraphComponent[0] );
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            graphComponent.setAlignedLayout( gc );
        }
    }
}
