package edu.colorado.phet.common.motion.graphs;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:21:40 AM
 */

public class GraphSetNode extends PNode {
    private GraphSetModel graphSetModel;
    private ArrayList<MinimizableControlGraph> graphComponents = new ArrayList<MinimizableControlGraph>();
    private MinimizableControlGraph.Listener graphComponentListener;
    private double width;
    private double height;

    private Layout flow = new Layout() {
        public void update() {
            setFlowLayout();
        }
    };
    private Layout aligned = new Layout() {
        public void update() {
            setAlignedLayout();
        }
    };
    private Layout layout = flow;

    public GraphSetNode( GraphSetModel graphSetModel ) {
        this.graphSetModel = graphSetModel;
        graphComponentListener = new MinimizableControlGraph.Listener() {
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

    public void forceRepaintGraphs() {
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            if ( minimizableControlGraph.getVisible() && !minimizableControlGraph.isMinimized() ) {
                minimizableControlGraph.forceUpdate();
            }
        }
    }

    static interface Layout {
        void update();
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        this.width = width;
        this.height = height;
        relayout();
        setOffset( x, y );
    }

    private void updateGraphSuite() {
        while ( graphComponents.size() > 0 ) {
            removeGraphComponent( 0 );
        }
        GraphSuite graphSuite = graphSetModel.getGraphSuite();
        for ( int i = 0; i < graphSuite.getGraphComponentCount(); i++ ) {
            addGraphComponent( graphSuite.getGraphComponent( i ) );
        }
        updateLayout();
        relayout();
    }

    private void addGraphComponent( MinimizableControlGraph minimizableControlGraph ) {
        graphComponents.add( minimizableControlGraph );
        addChild( minimizableControlGraph );
        minimizableControlGraph.addListener( graphComponentListener );
        updateLayout();
    }

    public void forceRelayout() {
        relayout();
    }

    private void relayout() {
        double yPad = 5;
        double availableY = height;
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            availableY -= minimizableControlGraph.getFixedHeight();
            availableY -= yPad;
        }

        double yOffset = 0;
        double graphHeight = Math.min( availableY / numMaximized(), availableY / 2 );//put a minimum on the vertical height of the graph.  Tall graphs were overwhelming.

        for ( int i = 0; i < graphComponents.size(); i++ ) {
            MinimizableControlGraph minimizableControlGraph = getGraphComponent( i );
            minimizableControlGraph.setOffset( 0, yOffset );
            if ( numMaximized() > 0 ) {
                minimizableControlGraph.setAvailableBounds( width, graphHeight );
            }
            yOffset += minimizableControlGraph.getFullBounds().getHeight() + yPad;
        }
        relayoutControlGraphs();

        forceRepaintGraphs();
    }

    private int numMaximized() {
        int count = 0;
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            count += minimizableControlGraph.isMinimized() ? 0 : 1;
        }
        return count;
    }

    private void removeGraphComponent( int i ) {
        MinimizableControlGraph minimizableControlGraph = graphComponents.remove( i );
        minimizableControlGraph.removeListener( graphComponentListener );
        removeChild( minimizableControlGraph );
        updateLayout();
    }

    private void updateLayout() {
        layout.update();
    }

    private MinimizableControlGraph getGraphComponent( int i ) {
        return graphComponents.get( i );
    }

    public void setFlowLayout() {
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            minimizableControlGraph.setFlowLayout();
        }
        this.layout = flow;
    }

    public void setAlignedLayout() {
        MinimizableControlGraph[] gcs = graphComponents.toArray( new MinimizableControlGraph[graphComponents.size()] );
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            minimizableControlGraph.setAlignedLayout( gcs );
        }
        relayoutControlGraphs();
        this.layout = aligned;
    }

    private void relayoutControlGraphs() {
        for ( MinimizableControlGraph minimizableControlGraph : graphComponents ) {
            minimizableControlGraph.relayoutControlGraph();
        }
    }
}
